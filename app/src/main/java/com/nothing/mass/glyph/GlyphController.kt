// GlyphController.kt
package com.nothing.mass.glyph

import android.content.Context
import com.nothing.ketchum.Common
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager
import com.nothing.mass.core.MassData
import com.nothing.mass.core.MassState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt
import kotlin.math.pow

class GlyphController(
    private val context: Context
) {
    private var glyphManager: GlyphManager? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentDotCount = 0
    private var currentBrightness = 0f
    private var isAnimating = false

    fun initialize(): Boolean {
        return try {
            glyphManager = GlyphManager.getInstance(context)
            glyphManager?.init { callback ->
                if (callback == null) {
                    true
                } else {
                    false
                }
            } ?: false
        } catch (e: GlyphException) {
            false
        }
    }

    fun observeMassData(massDataFlow: StateFlow<MassData>) {
        scope.launch {
            massDataFlow.collect { massData ->
                updateGlyph(massData)
            }
        }
    }

    private suspend fun updateGlyph(massData: MassData) {
        if (isAnimating) return
        withContext(Dispatchers.Main) {
            isAnimating = true
            try {
                animateToState(
                    targetDots = massData.activeDots,
                    targetBrightness = massData.state.brightness,
                    state = massData.state
                )
                if (massData.state == MassState.CRITICAL && massData.isAtLimit) {
                    startPulseAnimation()
                }
            } finally {
                isAnimating = false
            }
        }
    }

    private suspend fun animateToState(
        targetDots: Int,
        targetBrightness: Float,
        state: MassState
    ) {
        val frameDuration = 50L
        val totalDuration = 500L
        val frames = (totalDuration / frameDuration).toInt()
        val startDots = currentDotCount
        val startBrightness = currentBrightness
        for (frame in 0..frames) {
            val progress = frame.toFloat() / frames
            val easedProgress = easeInOutCubic(progress)
            val dots = (startDots + (targetDots - startDots) * easedProgress).roundToInt()
            val brightness = startBrightness + (targetBrightness - startBrightness) * easedProgress
            renderDotMatrix(dots, brightness)
            delay(frameDuration)
        }
        currentDotCount = targetDots
        currentBrightness = targetBrightness
    }

    private fun renderDotMatrix(activeDots: Int, brightness: Float) {
        try {
            val glyphFrame = GlyphManager.getInstance(context).glyphFrameBuilder
            val intensity = (brightness * 4095).toInt()
            val dotPattern = generateSpiralPattern(TOTAL_DOTS)
            for (i in 0 until TOTAL_DOTS) {
                val dotIndex = dotPattern[i]
                val dotIntensity = if (i < activeDots) intensity else 0
                glyphFrame.buildChannel(dotIndex, dotIntensity)
            }
            glyphFrame.build()
            glyphManager?.toggle(glyphFrame.frame)
        } catch (e: GlyphException) {
            // Handle glyph errors silently
        }
    }

    private fun generateSpiralPattern(totalDots: Int): IntArray {
        return intArrayOf(
            16,
            15, 17, 14, 18, 13, 19, 12, 20,
            11, 21, 10, 22, 9, 23, 8, 24,
            7, 25, 6, 26, 5, 27, 4, 28,
            3, 29, 2, 30, 1, 31, 0, 32
        )
    }

    private suspend fun startPulseAnimation() {
        scope.launch {
            repeat(3) {
                for (i in 0..10) {
                    val progress = i / 10f
                    val brightness = 0.7f + 0.3f * kotlin.math.sin(progress * Math.PI).toFloat()
                    renderDotMatrix(TOTAL_DOTS, brightness)
                    delay(80)
                }
            }
        }
    }

    private fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4 * t * t * t
        } else {
            1 - kotlin.math.pow(-2f * t + 2f, 3f) / 2f
        }
    }

    fun clear() {
        try {
            glyphManager?.turnOff()
            currentDotCount = 0
            currentBrightness = 0f
        } catch (e: GlyphException) {
            // Handle silently
        }
    }

    fun release() {
        scope.cancel()
        try {
            glyphManager?.unInit()
        } catch (e: GlyphException) {
            // Handle silently
        }
    }

    companion object {
        const val TOTAL_DOTS = 33
    }
}
