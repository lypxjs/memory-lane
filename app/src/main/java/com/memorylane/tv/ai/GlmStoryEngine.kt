package com.memorylane.tv.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.memorylane.tv.data.Photo
import com.memorylane.tv.data.loadAssetBitmap
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Narration via Zhipu BigModel GLM (vision-capable, no AWS/credit card needed).
 *
 * Product rule: the family's memoryNote is the ONLY source of truth. The AI
 * is a ghostwriter and broadcaster - it retells what a family member actually
 * recorded, in a warm voice, and it must not invent facts. Without a note
 * there is no story: the app says so instead of making one up.
 *
 * Degradation ladder, so the demo never dies on stage:
 *   1. photo attached  -> glm-4v-flash retells with the bitmap in view
 *   2. no photo/failed -> glm-4-flash retells from the note alone
 *   3. network failed  -> falls back to [MockStoryEngine]
 *
 * The API key is injected at build time from local.properties (gitignored).
 * Debug builds only - never publish an APK with a baked-in key.
 */
class GlmStoryEngine(
    private val context: Context,
    private val apiKey: String,
    private val fallback: StoryEngine = MockStoryEngine(),
) : StoryEngine {

    override suspend fun storyFor(photo: Photo): Story = withContext(Dispatchers.IO) {
        val note = photo.memoryNote
        if (note.isNullOrBlank()) {
            return@withContext Story(
                text = "This photo hasn't been told yet. Ask the family to record a " +
                    "memory for it - the story must be yours, not mine.",
                source = Story.Source.MOCK,
            )
        }
        // Family reference faces ride along so the narrator can say "Dad"
        // instead of "a man". Empty roster -> plain narration, no extra cost.
        val refs = com.memorylane.tv.data.FamilyStore.snapshot().mapNotNull { m ->
            com.memorylane.tv.data.SampleAlbum.photos
                .firstOrNull { it.id == m.refPhotoId }
                ?.assetPath
                ?.let { path ->
                    loadAssetBitmap(context.assets, path, maxDim = 320)?.let { bmp ->
                        Triple(m.name, encodeImage(bmp), bmp)
                    }
                }
        }
        try {
            val bitmap = photo.assetPath?.let { loadAssetBitmap(context.assets, it, maxDim = 640) }
            if (bitmap != null) {
                try {
                    val s = requestStory(photo, note, encodeImage(bitmap), VISION_MODEL, refs)
                    return@withContext s
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "vision attempt failed, falling to text", e)
                } finally {
                    bitmap.recycle()
                    refs.forEach { it.third.recycle() }
                }
            }
            requestStory(photo, note, imageBase64 = null, model = TEXT_MODEL, familyRefs = refs)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "text attempt failed, falling to mock", e)
            fallback.storyFor(photo)
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 82, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }

    private suspend fun requestStory(
        photo: Photo,
        memoryNote: String,
        imageBase64: String?,
        model: String,
        familyRefs: List<Triple<String, String, Bitmap>> = emptyList(),
    ): Story = suspendCancellableCoroutine { cont ->
        val userContent = JSONArray()
        // Reference faces first, each labelled, so the model can match the
        // target photo's faces against the roster by name.
        for ((name, refB64, _) in familyRefs) {
            userContent.put(
                JSONObject()
                    .put("type", "image_url")
                    .put("image_url", JSONObject().put("url", "data:image/jpeg;base64,$refB64"))
            )
            userContent.put(
                JSONObject()
                    .put("type", "text")
                    .put("text", "Reference photo: this family member is called $name.")
            )
        }
        if (imageBase64 != null) {
            userContent.put(
                JSONObject()
                    .put("type", "image_url")
                    .put("image_url", JSONObject().put("url", "data:image/jpeg;base64,$imageBase64"))
            )
        }
        userContent.put(
            JSONObject()
                .put("type", "text")
                .put("text", userPrompt(photo, memoryNote, familyRefs.map { it.first }))
        )

        val body = JSONObject()
            .put("model", model)
            .put(
                "messages",
                JSONArray()
                    .put(JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))
                    .put(JSONObject().put("role", "user").put("content", userContent))
            )
            .put("max_tokens", 400)
            .put("temperature", 0.7)

        val conn = URL("https://open.bigmodel.cn/api/paas/v4/chat/completions").openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 10_000
        conn.readTimeout = 30_000
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.doOutput = true

        try {
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = conn.responseCode
            val text = if (code in 200..299) conn.inputStream.readBytes()
            else conn.errorStream?.readBytes()
            if (code !in 200..299 || text == null) {
                cont.resumeWithException(IllegalStateException("GLM HTTP $code"))
                return@suspendCancellableCoroutine
            }
            val reply = JSONObject(String(text))
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
            if (reply.isEmpty()) {
                cont.resumeWithException(IllegalStateException("GLM empty reply"))
            } else {
                android.util.Log.i(TAG, "GLM story ok, model=$model, len=${reply.length}")
                val (people, storyText) = parsePeopleLine(reply)
                cont.resume(Story(text = storyText, source = Story.Source.GLM, people = people))
            }
        } catch (e: Exception) {
            if (cont.isActive) cont.resumeWithException(e)
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        private const val TAG = "GlmStory"
        private const val VISION_MODEL = "glm-4v-flash"
        private const val TEXT_MODEL = "glm-4-flash"

        private const val SYSTEM_PROMPT =
            "You are the narrator of Memory Lane, a TV photo album for an elderly " +
                "listener. A family member recorded a true memory about the attached " +
                "photo. Your job is to RETELL that recorded memory as a warm, spoken " +
                "story of 4-6 sentences. STRICT RULES: use only facts from the " +
                "family note and what is actually visible in the photo; never invent " +
                "people, events, dialogue or details that the note does not mention; " +
                "no markdown, no lists - flowing, gentle prose an elderly viewer " +
                "would love to hear from the couch."

        private fun userPrompt(photo: Photo, memoryNote: String, familyNames: List<String>): String {
            val base = "Photo title: \"${photo.title}\" (${photo.dateLabel}). " +
                "Family memory note (the true story to retell): \"$memoryNote\". "
            return if (familyNames.isEmpty()) {
                base + "Retell it warmly."
            } else {
                base +
                    "The images before the last one are reference photos of the family " +
                    "members: ${familyNames.joinToString(", ")}. The LAST image is the target " +
                    "photo. FIRST, on its own line, write exactly 'PEOPLE: ' followed by the " +
                    "names of those family members whose face you recognise in the target " +
                    "photo (comma-separated, or 'PEOPLE: none' if none). Then retell the " +
                    "memory warmly, using those names for the people in the photo when the " +
                    "note mentions them."
            }
        }

        /** Splits the structured first line ("PEOPLE: Dad, Sister") off the story. */
        private fun parsePeopleLine(reply: String): Pair<List<String>, String> {
            val firstBreak = reply.indexOf('\n')
            val firstLine = if (firstBreak == -1) reply else reply.substring(0, firstBreak).trim()
            val rest = if (firstBreak == -1) "" else reply.substring(firstBreak + 1).trim()
            if (!firstLine.startsWith("PEOPLE:")) return Pair(emptyList(), reply)
            val names = firstLine.removePrefix("PEOPLE:").split(',', '，')
                .map { it.trim().trimEnd('.', '。', '!', '！', '?', '？') }
                .filter { it.isNotEmpty() && !it.equals("none", ignoreCase = true) }
            return Pair(names, rest.ifBlank { reply })
        }
    }
}
