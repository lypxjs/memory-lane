package com.memorylane.tv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.data.Photo

/**
 * The wall of memories. D-pad moves focus between cards, OK opens a photo.
 */
@Composable
fun PhotoGridScreen(
    photos: List<Photo>,
    onPhotoClick: (Photo) -> Unit,
    onFamilyClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp, top = 32.dp, bottom = 16.dp, end = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Memory Lane",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Every photo has a story. Sit back and listen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
            PillButton(text = "👪 Our Family", onClick = onFamilyClick)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(start = 48.dp, end = 48.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(photos, key = { it.id }) { photo ->
                PhotoCard(photo = photo, onClick = { onPhotoClick(photo) })
            }
        }
    }
}

@Composable
private fun PhotoCard(photo: Photo, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(if (focused) 1.05f else 1f, label = "cardScale")
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .scale(cardScale)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (focused) 3.dp else 0.dp,
                    color = if (focused) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable(onClick = onClick)
                .onFocusChanged { focused = it.isFocused || it.hasFocus },
        ) {
            PhotoVisual(photo = photo, modifier = Modifier.fillMaxSize(), emojiSize = 40)
        }
        Text(
            text = photo.title,
            style = MaterialTheme.typography.titleSmall,
            color = if (focused) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
