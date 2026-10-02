package com.memorylane.tv.ai

import com.memorylane.tv.data.Photo

/** A short, warm narration generated for one photo. */
data class Story(
    val text: String,
    val source: Source,
    /** Family members the narrator recognised in the photo, e.g. ["Dad"]. */
    val people: List<String> = emptyList(),
) {
    enum class Source { MOCK, GLM, BEDROCK }
}

/**
 * Where the narration comes from. The UI only knows this interface, so the
 * engine implementation can be swapped without touching screens.
 */
interface StoryEngine {
    suspend fun storyFor(photo: Photo): Story
}
