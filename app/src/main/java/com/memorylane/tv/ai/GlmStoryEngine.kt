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
        try {
            val bitmap = photo.assetPath?.let { loadAssetBitmap(context.assets, it, maxDim = 640) }
            if (bitmap != null) {
                try {
                    return@withContext requestStory(photo, note, encodeImage(bitmap), VISION_MODEL)
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "vision attempt failed, falling to text", e)
                } finally {
                    bitmap.recycle()
                }
            }
            requestStory(photo, note, imageBase64 = null, model = TEXT_MODEL)
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
    ): Story = suspendCancellableCoroutine { cont ->
        val userContent = JSONArray()
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
                .put("text", userPrompt(photo, memoryNote))
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
                cont.resume(Story(text = reply, source = Story.Source.GLM))
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

        private fun userPrompt(photo: Photo, memoryNote: String) =
            "Photo title: \"${photo.title}\" (${photo.dateLabel}). " +
                "Family memory note (the true story to retell): \"$memoryNote\". " +
                "Retell it warmly."
    }
}
