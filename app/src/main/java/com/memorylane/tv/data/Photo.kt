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
    /**
     * The family's own recorded memory for this photo (what a child said about
     * it). THIS is the ground truth of the story; the AI only retells it.
     * Null means nobody has told this photo's story yet - and the app will
     * say so instead of making something up.
     */
    val memoryNote: String? = null,
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
        memoryNote = "Dad insisted we hike to this rock arch before sunrise. He said the light through the arch would be worth it. It was - but he complained about his knees the whole way down, and Mom counted every single one of his complaints out loud."
        ),
        Photo(
            assetPath = "photos/p02.jpg",
        id = "p02", title = "Autumn Tranquility",
            dateLabel = "October 2023 · Lake Forest",
            emoji = "🍂", colorStart = Color(0xff8c6239), colorEnd = Color(0xFFE0A458),
        memoryNote = "Our first trip after Mom retired. She stood on this road taking photos of the autumn trees for half an hour. Dad and I just waited by the car and ate oranges. Nobody wanted to be the one to rush her."
        ),
        Photo(
            assetPath = "photos/p03.jpg",
        id = "p03", title = "Sparkling Memories",
            dateLabel = "July 2023 · Backyard",
            emoji = "✨", colorStart = Color(0xff4a3b5c), colorEnd = Color(0xFFA3B18A),
        memoryNote = "New Year's Eve at grandma's courtyard. She held the sparkler herself this year and laughed like a kid when it crackled. Grandpa was banned from holding one after last year's small fire."
        ),
        Photo(
            assetPath = "photos/p04.jpg",
        id = "p04", title = "Freedom in Flight",
            dateLabel = "June 2023 · Central Park",
            emoji = "🕊️", colorStart = Color(0xff3e5c76), colorEnd = Color(0xFFE9C46A),
        memoryNote = "The morning we released birds at the temple fair. You were two years old and cried when they flew away. Now you call it your first lesson in letting go. Grandma kept the wooden cage as a souvenir."
        ),
        Photo(
            assetPath = "photos/p05.jpg",
        id = "p05", title = "Vintage Cars",
            dateLabel = "April 2022 · Urban Parking Lot",
            emoji = "🚗", colorStart = Color(0xff9c6b30), colorEnd = Color(0xFFF2CC8F),
        memoryNote = "Grandpa drove three hours to this vintage car meet. He restored a Jaguar like the brown one in his twenties - the years before I was born. He stood in front of it for a long time and didn't say much at all."
        ),
        Photo(
            assetPath = "photos/p06.jpg",
        id = "p06", title = "Quiet Cafe Moments",
            dateLabel = "April 2023 · Urban Café",
            emoji = "☕", colorStart = Color(0xff4f4a45), colorEnd = Color(0xFF84A98C),
        memoryNote = "Rainy afternoon in the city. Mom had never sat in a proper cafe, so we picked this one. She ordered the cheapest tea on the menu and then declared it tasted better than the one she makes at home."
        ),
        Photo(
            assetPath = "photos/p07.jpg",
        id = "p07", title = "Calm Before the Storm",
            dateLabel = "October 2023 · Coastal Shoreline",
            emoji = "🌊", colorStart = Color(0xff2f4f5f), colorEnd = Color(0xFFF4B8C1),
        memoryNote = "An hour before the storm the whole sky turned green-grey. We counted waves from the shelter while Dad timed the lightning with his old diver's watch. The fisherman on the left never came in. He knew something we didn't."
        ),
        Photo(
            assetPath = "photos/p08.jpg",
        id = "p08", title = "Golden Hour by the Bay",
            dateLabel = "July 2023 · San Francisco",
            emoji = "🌉", colorStart = Color(0xff7a5c2e), colorEnd = Color(0xFFEE9B00),
        memoryNote = "Our anniversary trip to San Francisco. The dog claimed the bench first, so we squeezed in beside him. We watched the Golden Gate turn gold - the same city where we were students with no money and all the time in the world."
        ),
        Photo(
            assetPath = "photos/p09.jpg",
        id = "p09", title = "Steel Structure",
            dateLabel = "July 2023 · City Park",
            emoji = "🏗️", colorStart = Color(0xff3c4048), colorEnd = Color(0xFFE8EDEA),
        memoryNote = "You stood under this steel frame and asked how it holds itself up. We ended up watching the engineers on the walkway for an hour. Your grandma said it was the best free museum she had ever visited."
        ),
        Photo(
            assetPath = "photos/p10.jpg",
        id = "p10", title = "Green Hills from Above",
            dateLabel = "June 2023 · Mountain Range",
            emoji = "⛰️", colorStart = Color(0xff3e5f3e), colorEnd = Color(0xFFB08D57),
        memoryNote = "The view from the cable car over the hills where Mom grew up. She pointed out the exact footpath she walked to school, forty years ago, in shoes with cardboard soles. She talked the entire ride."
        ),
        Photo(
            assetPath = "photos/p11.jpg",
        id = "p11", title = "Solitude in the Sunset",
            dateLabel = "October 2023 · Coastal Cliffs",
            emoji = "🌅", colorStart = Color(0xff5c3a4f), colorEnd = Color(0xFFD9B380),
        memoryNote = "Your sister's last evening before she moved abroad. She said she wasn't sad about leaving. The photo says otherwise. We miss her most at exactly this hour, when the light goes soft."
        ),
        Photo(
            assetPath = "photos/p12.jpg",
        id = "p12", title = "Architectural Marvel",
            dateLabel = "April 2023 · London",
            emoji = "🏛️", colorStart = Color(0xff46586a), colorEnd = Color(0xFF95D1CC),
        memoryNote = "London, day three. Grandma's feet hurt but she refused to rest. Standing under this dome she looked up and said: I could look at clever things all day. We let her take the lead for the rest of the trip."
        ),
        Photo(
            assetPath = "photos/p01.jpg",
        id = "p13", title = "Waiting for a Story",
            dateLabel = "Somewhere worth remembering",
            emoji = "🎙️", colorStart = Color(0xff2c3e50), colorEnd = Color(0xFF4CA1AF),
            // The honesty demo: nobody has told this photo's story yet, and the
            // app says so instead of inventing one. Record a memory to give it a voice.
        memoryNote = null,
        ),
        Photo(
            assetPath = "photos/p14.jpg",
        id = "p14", title = "Dad and His First Camera",
            dateLabel = "June 2019 · Back Home",
            emoji = "📷", colorStart = Color(0xff3a4a5c), colorEnd = Color(0xFF8DA9C4),
        memoryNote = "Dad, the week he finally admitted he needed glasses. He wore them for the portrait and then declared the camera had gotten worse since his day. We let him believe that."
        ),
        Photo(
            assetPath = "photos/p15.jpg",
        id = "p15", title = "Mom's Empty-Nest Afternoon",
            dateLabel = "May 2022 · Home",
            emoji = "🌼", colorStart = Color(0xff5c4a3a), colorEnd = Color(0xFFE0C3A0),
        memoryNote = "Mom laughed exactly like this the afternoon we redecorated her kitchen. She said the new curtains were too modern. She picked them herself the next weekend."
        ),
        Photo(
            assetPath = "photos/p16.jpg",
        id = "p16", title = "Sister Before the Flight",
            dateLabel = "August 2023 · Studio",
            emoji = "✈️", colorStart = Color(0xff6a5a2e), colorEnd = Color(0xFFF2D06B),
        memoryNote = "Sister had this portrait taken the week before she moved abroad. She said she wanted us to remember her looking calm. We remember everything else instead."
        ),
    )
}
