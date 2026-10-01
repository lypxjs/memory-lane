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
 * Degradation ladder, so the demo never dies on stage:
 *   1. photo attached  -> glm-4v-flash looks at the bitmap and writes the story
 *   2. no photo/failed -> glm-4-flash writes from title + date metadata
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
        try {
            val bitmap = photo.assetPath?.let { loadAssetBitmap(context.assets, it, maxDim = 640) }
            if (bitmap != null) {
                try {
                    return@withContext requestStory(photo, encodeImage(bitmap), VISION_MODEL)
                } catch (e: Exception) {
                    // fall through to the text-only attempt
                } finally {
                    bitmap.recycle()
                }
            }
            requestStory(photo, imageBase64 = null, model = TEXT_MODEL)
        } catch (e: Exception) {
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
                .put("text", userPrompt(photo))
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
            .put("temperature", 0.8)

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
                cont.resume(Story(text = reply, source = Story.Source.GLM))
            }
        } catch (e: Exception) {
            if (cont.isActive) cont.resumeWithException(e)
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        private const val VISION_MODEL = "glm-4v-flash"
        private const val TEXT_MODEL = "glm-4-flash"

        private const val SYSTEM_PROMPT =
            "You are Memory Lane, a warm family storyteller for a TV photo album. " +
                "Given a photo (or its title and date), write a 4-6 sentence story in " +
                "plain, gentle English that an elderly viewer would love to hear while " +
                "sitting on the couch. Reference what you actually see when a photo is " +
                "provided. No markdown, no lists, no headings - flowing prose only."

        private fun userPrompt(photo: Photo) =
            "This photo is titled \"${photo.title}\" (${photo.dateLabel}). " +
                "Tell its story."
    }
}
