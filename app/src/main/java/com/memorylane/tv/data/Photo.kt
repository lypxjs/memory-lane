package com.memorylane.tv.data

import androidx.compose.ui.graphics.Color

/**
 * A memory shown on the TV.
 *
 * [colorStart]/[colorEnd]/[emoji] stand in for the real bitmap while the app runs
 * on bundled sample content; once a photo backend exists they are replaced by an
 * image URI.
 */
data class Photo(
    val id: String,
    val title: String,
    val dateLabel: String,
    val emoji: String,
    val colorStart: Color,
    val colorEnd: Color,
    val assetPath: String? = null,
)

/**
 * Bundled demo content so the TV demo never depends on a network or a backend.
 * Warm, family-oriented scenes; the AI story layer treats them like real photos.
 */
object SampleAlbum {
    val photos: List<Photo> = listOf(
        Photo(
            assetPath = "photos/p01.jpg",
        id = "p01", title = "Ancient Archway",
            dateLabel = "October 2023 · Rocky Cliffs",
            emoji = "🏛️", colorStart = Color(0xff3e5c76), colorEnd = Color(0xFF9DB4C9),
        ),
        Photo(
            assetPath = "photos/p02.jpg",
        id = "p02", title = "Autumn Tranquility",
            dateLabel = "October 2023 · Lake Forest",
            emoji = "🍂", colorStart = Color(0xff8c6239), colorEnd = Color(0xFFE0A458),
        ),
        Photo(
            assetPath = "photos/p03.jpg",
        id = "p03", title = "Sparkling Memories",
            dateLabel = "July 2023 · Backyard",
            emoji = "✨", colorStart = Color(0xff4a3b5c), colorEnd = Color(0xFFA3B18A),
        ),
        Photo(
            assetPath = "photos/p04.jpg",
        id = "p04", title = "Freedom in Flight",
            dateLabel = "June 2023 · Central Park",
            emoji = "🕊️", colorStart = Color(0xff3e5c76), colorEnd = Color(0xFFE9C46A),
        ),
        Photo(
            assetPath = "photos/p05.jpg",
        id = "p05", title = "Vintage Cars",
            dateLabel = "April 2022 · Urban Parking Lot",
            emoji = "🚗", colorStart = Color(0xff9c6b30), colorEnd = Color(0xFFF2CC8F),
        ),
        Photo(
            assetPath = "photos/p06.jpg",
        id = "p06", title = "Quiet Cafe Moments",
            dateLabel = "April 2023 · Urban Café",
            emoji = "☕", colorStart = Color(0xff4f4a45), colorEnd = Color(0xFF84A98C),
        ),
        Photo(
            assetPath = "photos/p07.jpg",
        id = "p07", title = "Calm Before the Storm",
            dateLabel = "October 2023 · Coastal Shoreline",
            emoji = "🌊", colorStart = Color(0xff2f4f5f), colorEnd = Color(0xFFF4B8C1),
        ),
        Photo(
            assetPath = "photos/p08.jpg",
        id = "p08", title = "Golden Hour by the Bay",
            dateLabel = "July 2023 · San Francisco",
            emoji = "🌉", colorStart = Color(0xff7a5c2e), colorEnd = Color(0xFFEE9B00),
        ),
        Photo(
            assetPath = "photos/p09.jpg",
        id = "p09", title = "Steel Structure",
            dateLabel = "July 2023 · City Park",
            emoji = "🏗️", colorStart = Color(0xff3c4048), colorEnd = Color(0xFFE8EDEA),
        ),
        Photo(
            assetPath = "photos/p10.jpg",
        id = "p10", title = "Green Hills from Above",
            dateLabel = "June 2023 · Mountain Range",
            emoji = "⛰️", colorStart = Color(0xff3e5f3e), colorEnd = Color(0xFFB08D57),
        ),
        Photo(
            assetPath = "photos/p11.jpg",
        id = "p11", title = "Solitude in the Sunset",
            dateLabel = "October 2023 · Coastal Cliffs",
            emoji = "🌅", colorStart = Color(0xff5c3a4f), colorEnd = Color(0xFFD9B380),
        ),
        Photo(
            assetPath = "photos/p12.jpg",
        id = "p12", title = "Architectural Marvel",
            dateLabel = "April 2023 · London",
            emoji = "🏛️", colorStart = Color(0xff46586a), colorEnd = Color(0xFF95D1CC),
        ),
    )
}
