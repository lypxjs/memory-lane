package com.memorylane.tv.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.data.Photo
import com.memorylane.tv.data.loadAssetBitmap

/**
 * A memory on screen: the real bundled photo when present, otherwise a warm
 * gradient placeholder. One composable reused by the grid card and detail hero.
 */
@Composable
fun PhotoVisual(
    photo: Photo,
    modifier: Modifier = Modifier,
    showCaption: Boolean = true,
    emojiSize: Int = 48,
) {
    val context = LocalContext.current
    val bitmap = remember(photo.id) {
        photo.assetPath?.let { loadAssetBitmap(context.assets, it, maxDim = 1280) }
    }

    Box(
        modifier = modifier
            .background(Brush.linearGradient(listOf(photo.colorStart, photo.colorEnd)))
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(text = photo.emoji, style = TextStyle(fontSize = emojiSize.sp))
        }
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
