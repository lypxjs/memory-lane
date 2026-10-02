package com.memorylane.tv.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.ai.GlmAsrEngine
import com.memorylane.tv.data.NoteStore
import com.memorylane.tv.data.Photo
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The soul of the product: a family member records the TRUE memory in their
 * own voice (any language), GLM-ASR turns it into text, the text becomes the
 * photo's memory note and the story engine retells it.
 *
 * Devices without a microphone (Fire TV Stick) skip recording and type the
 * note instead. If ASR fails - no balance, no network - the transcript field
 * simply stays empty for typing: the demo never dies on this step.
 */
@Composable
fun RecordMemoryScreen(
    photo: Photo,
    apiKey: String,
    onSaved: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hasMic = remember {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted = it }

    var recording by remember { mutableStateOf(false) }
    var recordedWav by remember { mutableStateOf<ByteArray?>(null) }
    var transcribing by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf(NoteStore.effectiveNote(photo) ?: "") }

    // --- recorder loop: 16kHz mono PCM -> WAV bytes (glm-asr-native container) ---
    val sampleRate = 16_000
    val maxSeconds = 90

    fun stopRecorder() { recording = false }

    if (hasMic && !granted) {
        LaunchedEffect(Unit) { permLauncher.launch(Manifest.permission.RECORD_AUDIO) }
    }

    LaunchedEffect(recording) {
        if (!recording || !granted || !hasMic) return@LaunchedEffect
        statusText = "Recording… tell this photo's memory in your own words."
        val collected = withContext(Dispatchers.IO) {
            val minBuf = AudioRecord.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
            )
            var rec: AudioRecord? = null
            try {
                rec = AudioRecord(
                    MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    minBuf * 2,
                )
                rec.startRecording()
                val buf = ShortArray(sampleRate) // 1s per read
                val pcm = java.io.ByteArrayOutputStream()
                var seconds = 0
                while (recording && seconds < maxSeconds) {
                    val n = rec.read(buf, 0, buf.size)
                    if (n > 0) {
                        for (i in 0 until n) {
                            pcm.write(buf[i].toInt() and 0xff)
                            pcm.write((buf[i].toInt() shr 8) and 0xff)
                        }
                        seconds++
                    }
                }
                pcm.toByteArray()
            } finally {
                try { rec?.stop() } catch (_: Exception) {}
                try { rec?.release() } catch (_: Exception) {}
            }
        }
        recordedWav = com.memorylane.tv.ai.Wav.header(collected.size, sampleRate) + collected
        statusText = "Recorded ${collected.size / (2 * sampleRate)}s. Ready to turn into words."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Tell \"${photo.title}\"",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = if (hasMic)
                "Speak the true memory behind this photo - in any language. Your voice becomes the note; the AI only retells it."
            else
                "This device has no microphone. Type the true memory behind this photo - the AI only retells it.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        )

        if (hasMic && granted) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PillButton(
                    text = when {
                        recording -> "■ Stop recording"
                        recordedWav != null -> "● Record again"
                        else -> "● Start recording"
                    },
                    onClick = {
                        if (recording) stopRecorder() else {
                            recordedWav = null
                            recording = true
                        }
                    },
                )
                if (recordedWav != null && !recording) {
                    PillButton(
                        text = if (transcribing) "Listening…" else "Turn voice into words",
                        onClick = {
                            transcribing = true
                            statusText = ""
                            scope.launch {
                                val transcript = GlmAsrEngine(apiKey)
                                    .transcribe(recordedWav ?: return@launch)
                                transcribing = false
                                if (transcript != null) {
                                    noteText = transcript
                                    statusText = "Got it. Check the words, then save."
                                } else {
                                    statusText = "Voice-to-text unavailable (top up glm-asr or go offline). Type or fix the note below instead."
                                }
                            }
                        },
                    )
                }
            }
        }

        if (statusText.isNotEmpty()) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // The note itself - always editable, also the typed-note path for mic-less TVs.
        // Hand-rolled like PillButton: tv-material has no text field, and
        // foundation's BasicTextField handles touch and D-pad alike.
        BasicTextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            minLines = 4,
            maxLines = 8,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PillButton(
                text = "Save this memory",
                onClick = {
                    val wav = recordedWav
                    val audioFile: File? = wav?.let {
                        val f = File(context.filesDir, "audio/${photo.id}.wav")
                        f.parentFile?.mkdirs()
                        f.writeBytes(it)
                        f
                    }
                    NoteStore.set(context, photo.id, noteText.trim(), audioFile?.absolutePath)
                    onSaved()
                },
            )
            PillButton(text = "Cancel", onClick = onBack)
        }

        Text(
            text = "The story must be yours. Memory Lane never invents what the family did not say.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.45f),
        )
    }
}
