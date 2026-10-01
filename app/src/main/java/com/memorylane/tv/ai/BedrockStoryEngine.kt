package com.memorylane.tv.ai

import com.memorylane.tv.data.Photo

/**
 * Bedrock-backed narration engine. NOT wired up yet.
 *
 * Plan (once an AWS account + credentials exist):
 *  1. Add `aws-sdk-kotlin` (bedrockruntime + auth) to app/build.gradle.kts.
 *  2. Invoke Amazon Nova Lite with a photo reference and a narration prompt:
 *     "You are a warm family storyteller. Given this photo taken on <dateLabel>,
 *     write a 4-6 sentence story an elderly viewer would love. Plain prose,
 *     no markdown."
 *  3. Stream tokens back so the story types itself onto the TV.
 *  4. Qualifies this project for the hackathon's AWS Builder mini challenge;
 *     document the integration in the submission.
 */
class BedrockStoryEngine : StoryEngine {
    override suspend fun storyFor(photo: Photo): Story {
        throw NotImplementedError("Waiting on AWS credentials - see class docs")
    }
}
