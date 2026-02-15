// ThresholdBehavior.kt
package com.nothing.mass.ux

import com.nothing.mass.glyph.GlyphController
import kotlinx.coroutines.delay

object ThresholdBehavior {
    fun handle80Percent(glyphController: GlyphController) {
        glyphController.singlePulse(
            duration = 1000L,
            intensity = 0.7f
        )
    }

    fun handle95Percent(glyphController: GlyphController) {
        glyphController.doublePulse(
            duration = 800L,
            intensity = 0.9f
        )
    }

    fun handle100Percent(glyphController: GlyphController) {
        glyphController.startBreathingAnimation(
            minBrightness = 0.6f,
            maxBrightness = 1.0f,
            cycleDuration = 3000L
        )
    }
}

suspend fun GlyphController.singlePulse(duration: Long, intensity: Float) {
    animateBrightness(currentBrightness, intensity, duration / 2)
    delay(100)
    animateBrightness(intensity, currentBrightness, duration / 2)
}

suspend fun GlyphController.doublePulse(duration: Long, intensity: Float) {
    repeat(2) {
        singlePulse(duration / 2, intensity)
        delay(200)
    }
}

suspend fun GlyphController.startBreathingAnimation(
    minBrightness: Float,
    maxBrightness: Float,
    cycleDuration: Long
) {
    // Implementation would go here
}

// Extension functions that would need actual implementation
suspend fun GlyphController.animateBrightness(from: Float, to: Float, duration: Long) {
    // Animation logic
}

val GlyphController.currentBrightness: Float
    get() = 0f // Placeholder
