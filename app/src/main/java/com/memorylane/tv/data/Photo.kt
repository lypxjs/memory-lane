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
)

/**
 * Bundled demo content so the TV demo never depends on a network or a backend.
 * Warm, family-oriented scenes; the AI story layer treats them like real photos.
 */
object SampleAlbum {
    val photos: List<Photo> = listOf(
        Photo(
            id = "p01", title = "Morning Fog on the Lake",
            dateLabel = "July 2019 · Qiandao Lake",
            emoji = "🎣", colorStart = Color(0xFF3E5C76), colorEnd = Color(0xFF9DB4C9),
        ),
        Photo(
            id = "p02", title = "Grandpa's 70th Birthday",
            dateLabel = "October 2019 · Home",
            emoji = "🎂", colorStart = Color(0xFF8C4A32), colorEnd = Color(0xFFE0A458),
        ),
        Photo(
            id = "p03", title = "First Day of School",
            dateLabel = "September 2021 · School Gate",
            emoji = "🎒", colorStart = Color(0xFF4A6C4F), colorEnd = Color(0xFFA3B18A),
        ),
        Photo(
            id = "p04", title = "Dumplings for New Year",
            dateLabel = "February 2020 · Grandma's Kitchen",
            emoji = "🥟", colorStart = Color(0xFF9C3D2E), colorEnd = Color(0xFFE9C46A),
        ),
        Photo(
            id = "p05", title = "The Whole Family at the Beach",
            dateLabel = "August 2022 · Sanya",
            emoji = "🏖️", colorStart = Color(0xFF2A7F9E), colorEnd = Color(0xFFF2CC8F),
        ),
        Photo(
            id = "p06", title = "Learning to Ride a Bike",
            dateLabel = "May 2021 · Community Park",
            emoji = "🚲", colorStart = Color(0xFF52796F), colorEnd = Color(0xFF84A98C),
        ),
        Photo(
            id = "p07", title = "Grandma's Garden in Bloom",
            dateLabel = "April 2023 · Balcony",
            emoji = "🌸", colorStart = Color(0xFF7D4F7D), colorEnd = Color(0xFFF4B8C1),
        ),
        Photo(
            id = "p08", title = "Hotpot Night",
            dateLabel = "November 2022 · Downtown",
            emoji = "🍲", colorStart = Color(0xFF9B2226), colorEnd = Color(0xFFEE9B00),
        ),
        Photo(
            id = "p09", title = "First Snowman",
            dateLabel = "December 2022 · Courtyard",
            emoji = "⛄", colorStart = Color(0xFF4F6D7A), colorEnd = Color(0xFFE8EDEA),
        ),
        Photo(
            id = "p10", title = "Graduation Day",
            dateLabel = "June 2024 · University",
            emoji = "🎓", colorStart = Color(0xFF3A405A), colorEnd = Color(0xFFB08D57),
        ),
        Photo(
            id = "p11", title = "Tea with Great-Grandma",
            dateLabel = "March 2023 · Living Room",
            emoji = "🍵", colorStart = Color(0xFF6F5233), colorEnd = Color(0xFFD9B380),
        ),
        Photo(
            id = "p12", title = "Kite Festival",
            dateLabel = "April 2024 · Riverside Meadow",
            emoji = "🪁", colorStart = Color(0xFF2E6F95), colorEnd = Color(0xFF95D1CC),
        ),
    )
}
