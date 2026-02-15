// AnimationConfig.kt
package com.nothing.mass.ux

object AnimationConfig {
    const val DOT_FADE_IN = 200L
    const val DOT_FADE_OUT = 150L
    const val STATE_TRANSITION = 500L
    const val BRIGHTNESS_FADE = 300L
    const val PULSE_DURATION = 1000L
    const val BREATHING_CYCLE = 3000L

    val EASE_IN_OUT_CUBIC: (Float) -> Float = { t ->
        if (t < 0.5f) {
            4 * t * t * t
        } else {
            1 - kotlin.math.pow(-2f * t + 2f, 3f) / 2f
        }
    }

    val EASE_OUT_SINE: (Float) -> Float = { t ->
        kotlin.math.sin((t * Math.PI) / 2).toFloat()
    }

    val EASE_IN_OUT_SINE: (Float) -> Float = { t ->
        -(kotlin.math.cos(Math.PI * t) - 1).toFloat() / 2
    }

    const val BRIGHTNESS_LIGHT = 0.3f
    const val BRIGHTNESS_DENSE = 0.6f
    const val BRIGHTNESS_CRITICAL = 1.0f
    const val BRIGHTNESS_OFF = 0.0f

    const val TARGET_FPS = 20
    const val FRAME_DURATION_MS = 1000L / TARGET_FPS
}
