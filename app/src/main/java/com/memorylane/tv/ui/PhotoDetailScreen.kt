package com.memorylane.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.ai.Story
import com.memorylane.tv.ai.StoryEngine
import com.memorylane.tv.data.Photo

/**
 * One memory, fullscreen: the photo on the left, its AI narration on the right.
 * [engineKey] forces a fresh story when the user taps "Tell me again".
 */
@Composable
fun PhotoDetailScreen(
    photo: Photo,
    storyEngine: StoryEngine,
    onBack: () -> Unit,
) {
    var engineKey by remember { mutableStateOf(0) }

    // Cache stories per photo+generation so back-and-forth navigation is instant.
    val storyState = produceState<Story?>(initialValue = null, photo.id, engineKey) {
        value = storyEngine.storyFor(photo)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(photo.colorStart, photo.colorEnd))
            )
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
                color = androidx.compose.ui.graphics.Color.White,
            )
            Text(
                text = photo.dateLabel,
                style = MaterialTheme.typography.titleSmall,
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.75f),
            )

            val story = storyState.value
            if (story == null) {
                Text(
                    text = "Memory Lane is writing…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                )
            } else {
                Text(
                    text = story.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { engineKey++ }) {
                    Text("Tell me again")
                }
                Button(onClick = onBack) {
                    Text("Back to album")
                }
            }
        }
    }
}
