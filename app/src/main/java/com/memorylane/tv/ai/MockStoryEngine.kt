package com.memorylane.tv.ai

import com.memorylane.tv.data.Photo
import kotlinx.coroutines.delay

/**
 * Offline implementation used for development and for the demo until the
 * Bedrock-backed engine is wired in. Deterministic per photo, with a short
 * delay so the "writing…" state is visible, like the real thing.
 */
class MockStoryEngine : StoryEngine {

    override suspend fun storyFor(photo: Photo): Story {
        delay(1200)
        val body = TEMPLATE.format(photo.title, photo.dateLabel, photo.emoji)
        return Story(text = body, source = Story.Source.MOCK)
    }

    companion object {
        private val TEMPLATE =
            "%1\$s. %2\$s.%n%n" +
                "Some photos hold whole afternoons inside them. Look closely at this one: " +
                "the light, the faces leaning toward each other, the small mess no one " +
                "thought to tidy. %3\$s%n%n" +
                "Days like this don't announce themselves while they happen. It's only from " +
                "the couch, years later, that you notice: this was one of the good ones."
    }
}
