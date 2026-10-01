package com.memorylane.tv.ai

import com.memorylane.tv.data.Photo

/** A short, warm narration generated for one photo. */
data class Story(
    val text: String,
    val source: Source,
) {
    enum class Source { MOCK, BEDROCK }
}

/**
 * Where the narration comes from. The UI only knows this interface, so the
 * Bedrock-backed implementation can be swapped in without touching screens.
 */
interface StoryEngine {
    suspend fun storyFor(photo: Photo): Story
}
