package com.memorylane.tv.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * Wraps the on-device TTS engine so stories are SPOKEN, not just shown.
 * Initialization is async; speak() before ready is a no-op, and [speak]
 * flushes any ongoing narration.
 */
class TtsPlayer(context: Context) {

    private var tts: TextToSpeech? = null

    @Volatile
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                tts?.language = Locale.US
                Log.i(TAG, "TTS ready, engine=" + (tts?.voice?.name ?: "?"))
            } else {
                Log.e(TAG, "TTS init failed: $status")
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.i(TAG, "TTS speaking…")
            }

            override fun onDone(utteranceId: String?) {
                Log.i(TAG, "TTS done")
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e(TAG, "TTS error")
            }
        })
    }

    fun speak(text: String) {
        if (!ready) {
            Log.w(TAG, "speak() before TTS ready - skipped")
            return
        }
        tts?.stop()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        ready = false
    }

    companion object {
        private const val TAG = "MemoryLaneTts"
        private const val UTTERANCE_ID = "story"
    }
}
