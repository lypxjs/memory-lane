package com.memorylane.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.ai.Story
import com.memorylane.tv.ai.StoryEngine
import com.memorylane.tv.data.Photo

/**
 * One memory, fullscreen: the photo on the left, its narration on the right.
 * Stories are SPOKEN via on-device TTS as well as shown - the target viewer
 * listens from the couch. [engineKey] forces a fresh story on "Tell me again".
 */
@Composable
fun PhotoDetailScreen(
    photo: Photo,
    storyEngine: StoryEngine,
    onRecord: () -> Unit,
    onBack: () -> Unit,
) {
    var engineKey by remember { mutableStateOf(0) }

    // Cache stories per photo+note+generation so back-and-forth navigation is
    // instant, and a freshly recorded family note retells immediately.
    val storyState = produceState<Story?>(initialValue = null, photo.id, engineKey, photo.memoryNote) {
        value = storyEngine.storyFor(photo)
    }

    // The soul of the product: stories are spoken, not just shown.
    val context = LocalContext.current
    val tts = remember { TtsPlayer(context) }
    var muted by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        onDispose { tts.shutdown() }
    }
    LaunchedEffect(storyState.value?.text, muted) {
        val text = storyState.value?.text
        if (text != null && !muted) tts.speak(text)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(photo.colorStart, photo.colorEnd)))
            .padding(48.dp),
        horizontalArrangement = Arrangement.spacedBy(48.dp),
    ) {
        // hero photo
        PhotoVisual(
            photo = photo,
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp)),
            showCaption = false,
            emojiSize = 120,
        )

        // story panel
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = photo.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
            Text(
                text = photo.dateLabel,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.75f),
            )

            val story = storyState.value
            if (story == null) {
                Text(
                    text = "Memory Lane is writing…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                )
            } else {
                Text(
                    text = story.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (story.people.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        story.people.forEach { name ->
                            Text(
                                text = "👤 $name",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                if (photo.memoryNote != null) {
                    Text(
                        text = "Retold from a family recording - the AI only polished the words.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                    )
                }
            }

            // Horizontal scroll keeps every action reachable on narrow phone
            // screens; on a TV the row fits and the scroll never engages.
            @Suppress("ComposeModifierMissing")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                PillButton(text = "Tell me again", onClick = { engineKey++ })
                PillButton(text = "🎙 Record a memory", onClick = onRecord)
                PillButton(
                    text = if (muted) "🔊 Listen" else "🔇 Quiet",
                    onClick = {
                        muted = !muted
                        if (muted) {
                            tts.stop()
                        } else {
                            storyState.value?.text?.let { tts.speak(it) }
                        }
                    },
                )
                PillButton(text = "Back to album", onClick = onBack)
            }
        }
    }
}
