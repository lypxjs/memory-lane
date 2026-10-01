package com.memorylane.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.data.Photo

/**
 * Stands in for a real photo bitmap: warm gradient + scene emoji + caption.
 * One composable reused by the grid card and the detail hero.
 */
@Composable
fun PhotoVisual(
    photo: Photo,
    modifier: Modifier = Modifier,
    showCaption: Boolean = true,
    emojiSize: Int = 48,
) {
    Box(
        modifier = modifier
            .background(Brush.linearGradient(listOf(photo.colorStart, photo.colorEnd)))
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = photo.emoji,
            style = TextStyle(fontSize = emojiSize.sp),
        )
        if (showCaption) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
            ) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                )
                Text(
                    text = photo.dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
        }
    }
}
