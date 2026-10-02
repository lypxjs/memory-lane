package com.memorylane.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.memorylane.tv.data.FamilyStore
import com.memorylane.tv.data.Photo

/**
 * "Our Family" - the roster the narrator knows by name.
 *
 * Add a member: type the name, then tap the album photo that shows their
 * face clearly. That photo travels with every narration request as the
 * reference, so the story can say "Dad" instead of "a man".
 */
@Composable
fun FamilyScreen(
    photos: List<Photo>,
    onBack: () -> Unit,
) {
    var adding by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Our Family",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "These are the people Memory Lane can name in a story. " +
                "Reference photos are only sent with narration requests.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )

        if (!adding) {
            PillButton(text = "＋ Add family member", onClick = { adding = true })
        } else {
            Text(
                text = "New member",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            com.memorylane.tv.ui.FamilyNameField(value = name, onValueChange = { name = it })
            Text(
                text = "Tap the photo that shows their face clearly:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f, fill = false),
            ) {
                items(photos, key = { it.id }) { photo ->
                    FacePickCard(photo = photo) {
                        if (name.isNotBlank()) {
                            FamilyStore.add(name, photo.id)
                            name = ""
                            adding = false
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PillButton(text = "Cancel", onClick = { adding = false; name = "" })
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(FamilyStore.snapshot(), key = { it.id }) { m ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    val ref = photos.firstOrNull { it.id == m.refPhotoId }
                    if (ref != null) {
                        PhotoVisual(
                            photo = ref,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            emojiSize = 20,
                        )
                    }
                    Text(
                        text = m.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    PillButton(text = "Remove", onClick = { FamilyStore.remove(m.id) })
                }
            }
        }

        PillButton(text = "Back to album", onClick = onBack)
    }
}

/** Single-line dark-theme text input, hand-rolled like PillButton. */
@Composable
fun FamilyNameField(value: String, onValueChange: (String) -> Unit) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        ),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(
                    text = "Their name - Dad, Sister, Grandma…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.4f),
                )
            }
            inner()
        },
    )
}

@Composable
private fun FacePickCard(photo: Photo, onClick: () -> Unit) {    var focused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .aspectRatio(16f / 10f)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (focused) 3.dp else 0.dp,
                color = if (focused) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onClick)
            .onFocusChanged { focused = it.isFocused || it.hasFocus },
    ) {
        PhotoVisual(photo = photo, modifier = Modifier.fillMaxSize(), emojiSize = 24)
    }
}
