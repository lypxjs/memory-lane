package com.memorylane.tv.ai

import android.content.Context
import com.memorylane.tv.BuildConfig

/** Picks the richest story engine available at runtime. */
object StoryEngines {
    fun create(context: Context): StoryEngine =
        if (BuildConfig.GLM_API_KEY.isNotBlank()) {
            GlmStoryEngine(context.applicationContext, BuildConfig.GLM_API_KEY)
        } else {
            MockStoryEngine()
        }
}
