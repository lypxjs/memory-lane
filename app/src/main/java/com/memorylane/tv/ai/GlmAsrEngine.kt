package com.memorylane.tv.ai

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
 * Speech-to-text via Zhipu BigModel GLM-ASR (glm-asr, RMB billing ~0.06/min).
 *
 * Same vendor as the story engine on purpose: one RMB-payable account covers
 * the whole pipeline, no foreign card involved. If the call fails for any
 * reason (no balance, no network, quota) the caller falls back to typing the
 * note by hand - the demo must never die on the recording step.
 */
class GlmAsrEngine(private val apiKey: String) {

    /** Raw transcript text, or null on any failure. */
    suspend fun transcribe(wavBytes: ByteArray): String? = withContext(Dispatchers.IO) {
        try {
            request(wavBytes)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "glm-asr failed: ${e.message}")
            null
        }
    }

    private suspend fun request(wavBytes: ByteArray): String =
        suspendCancellableCoroutine { cont ->
            val boundary = "memorylane-${System.currentTimeMillis().toString(16)}"
            val head = "--$boundary\r\n".toByteArray() +
                "Content-Disposition: form-data; name=\"model\"\r\n\r\nglm-asr\r\n".toByteArray()
            val fileHead = ("--$boundary\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"memory.wav\"\r\n" +
                "Content-Type: audio/wav\r\n\r\n").toByteArray()
            val tail = "\r\n--$boundary--\r\n".toByteArray()
            val body = head + fileHead + wavBytes + tail

            val conn = URL(ENDPOINT).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 10_000
            conn.readTimeout = 60_000
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.doOutput = true
            conn.setFixedLengthStreamingMode(body.size)

            try {
                conn.outputStream.use { it.write(body) }
                val code = conn.responseCode
                val text = if (code in 200..299) conn.inputStream.readBytes()
                else conn.errorStream?.readBytes()
                if (code !in 200..299 || text == null) {
                    cont.resumeWithException(IllegalStateException("ASR HTTP $code: ${text?.toString(Charsets.UTF_8)?.take(200)}"))
                    return@suspendCancellableCoroutine
                }
                val transcript = JSONObject(String(text)).optString("text", "")
                if (transcript.isBlank()) {
                    cont.resumeWithException(IllegalStateException("ASR empty transcript"))
                } else {
                    android.util.Log.i(TAG, "glm-asr ok, len=${transcript.length}")
                    cont.resume(transcript.trim())
                }
            } catch (e: Exception) {
                if (cont.isActive) cont.resumeWithException(e)
            } finally {
                conn.disconnect()
            }
        }

    companion object {
        private const val TAG = "GlmAsr"
        private const val ENDPOINT = "https://open.bigmodel.cn/api/paas/v4/audio/transcriptions"
    }
}

/** 16kHz mono PCM recorder output wrapped in a WAV container - glm-asr-native. */
object Wav {

    fun header(pcmSize: Int, sampleRate: Int = 16_000, channels: Int = 1, bits: Int = 16): ByteArray {
        val byteRate = sampleRate * channels * bits / 8
        val blockAlign = channels * bits / 8
        val out = ByteArrayOutputStream(44)
        fun le16(v: Int) { out.write(v and 0xff); out.write((v shr 8) and 0xff) }
        fun le32(v: Int) {
            out.write(v and 0xff); out.write((v shr 8) and 0xff)
            out.write((v shr 16) and 0xff); out.write((v shr 24) and 0xff)
        }
        out.write("RIFF".toByteArray()); le32(36 + pcmSize)
        out.write("WAVE".toByteArray())
        out.write("fmt ".toByteArray()); le32(16); le16(1); le16(channels)
        le32(sampleRate); le32(byteRate); le16(blockAlign); le16(bits)
        out.write("data".toByteArray()); le32(pcmSize)
        return out.toByteArray()
    }
}
