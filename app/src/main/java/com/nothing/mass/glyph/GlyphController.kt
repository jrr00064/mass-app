// GlyphController.kt
package com.nothing.mass.glyph

import android.content.Context
import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphMatrixManager
import com.nothing.ketchum.GlyphMatrixFrame
import com.nothing.ketchum.GlyphMatrixObject
import com.nothing.mass.core.MassData
import com.nothing.mass.core.MassState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.min

/**
 * Controls GlyphMatrix rendering based on mass data using the Nothing Phone 3 (25x25) API
 */
class GlyphController(
    private val context: Context
) {
    private var glyphManager: GlyphMatrixManager? = null
    private var callback: GlyphMatrixManager.Callback? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentFillPercentage = 0f
    private var isAnimating = false
    private var isInitialized = false

    /**
     * Initialize GlyphMatrix interface
     */
    fun initialize(): Boolean {
        return try {
            glyphManager = GlyphMatrixManager.getInstance(context)
            callback = object : GlyphMatrixManager.Callback {
                override fun onServiceConnected(componentName: ComponentName) {
                    glyphManager?.register(Glyph.DEVICE_23112) // Phone 3
                    isInitialized = true
                }

                override fun onServiceDisconnected(componentName: ComponentName) {
                    isInitialized = false
                }
            }
            glyphManager?.init(callback)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Observe mass data and update glyph accordingly
     */
    fun observeMassData(massDataFlow: StateFlow<MassData>) {
        scope.launch {
            massDataFlow.collect { massData ->
                if (isInitialized) {
                    updateGlyph(massData)
                }
            }
        }
    }

    /**
     * Update glyph matrix based on mass data
     */
    private suspend fun updateGlyph(massData: MassData) {
        if (isAnimating) return
        withContext(Dispatchers.Main) {
            isAnimating = true
            try {
                animateToState(
                    targetPercentage = massData.percentageFilled,
                    targetBrightness = massData.state.brightness,
                    state = massData.state
                )

                // Start pulse animation if critical
                if (massData.state == MassState.CRITICAL && massData.isAtLimit) {
                    startPulseAnimation()
                }
            } finally {
                isAnimating = false
            }
        }
    }

    /**
     * Animate smooth transition between states
     */
    private suspend fun animateToState(
        targetPercentage: Float,
        targetBrightness: Float,
        state: MassState
    ) {
        val frameDuration = 50L // 20 FPS
        val totalDuration = 500L
        val frames = (totalDuration / frameDuration).toInt()
        val startPercentage = currentFillPercentage

        for (frame in 0..frames) {
            val progress = frame.toFloat() / frames
            val easedProgress = easeInOutCubic(progress)
            val percentage = startPercentage + (targetPercentage - startPercentage) * easedProgress
            val brightness = if (frame == 0) targetBrightness else targetBrightness

            renderMatrixFill(percentage, brightness)
            delay(frameDuration)
        }

        currentFillPercentage = targetPercentage
    }

    /**
     * Render the 25x25 matrix with circular fill pattern
     */
    private fun renderMatrixFill(percentage: Float, brightness: Float) {
        try {
            val frameBuilder = GlyphMatrixFrame.Builder()

            // Create a bitmap representing the fill pattern
            val fillBitmap = createFillBitmap(percentage, brightness)

            // Create glyph object from bitmap
            val glyphObject = GlyphMatrixObject.Builder()
                .setImageSource(fillBitmap)
                .setPosition(0, 0)
                .setScale(100)
                .setOrientation(0)
                .setBrightness((brightness * 255).toInt())
                .build()

            // Add to frame and display
            frameBuilder.addTop(glyphObject)
            val frame = frameBuilder.build(context)

            glyphManager?.setMatrixFrame(frame.render())
        } catch (e: Exception) {
            // Handle errors silently
        }
    }

    /**
     * Create a 25x25 bitmap with circular fill pattern
     * Fills from center outward based on percentage
     */
    private fun createFillBitmap(percentage: Float, brightness: Float): Bitmap {
        val size = 25
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = false
        }

        val centerX = size / 2f
        val centerY = size / 2f
        val maxRadius = size / 2f

        // Calculate fill radius based on percentage
        val fillRadius = maxRadius * percentage

        // Draw filled area
        val intensity = (brightness * 255).toInt()
        val color = Color.rgb(intensity, intensity, intensity)

        for (y in 0 until size) {
            for (x in 0 until size) {
                val distance = kotlin.math.hypot(x - centerX, y - centerY)
                if (distance <= fillRadius) {
                    bitmap.setPixel(x, y, color)
                } else {
                    bitmap.setPixel(x, y, Color.BLACK)
                }
            }
        }

        return bitmap
    }

    /**
     * Critical state pulse animation
     */
    private suspend fun startPulseAnimation() {
        scope.launch {
            repeat(3) {
                for (i in 0..10) {
                    val progress = i / 10f
                    val pulseBrightness = 0.7f + 0.3f * kotlin.math.sin(progress * Math.PI).toFloat()
                    renderMatrixFill(1.0f, pulseBrightness)
                    delay(80)
                }
            }
        }
    }

    /**
     * Cubic easing function for smooth animations
     */
    private fun easeInOutCubic(t: Float): Float {
        return if (t < 0.5f) {
            4 * t * t * t
        } else {
            1 - kotlin.math.pow(-2f * t + 2f, 3f) / 2f
        }
    }

    /**
     * Clear glyph display
     */
    fun clear() {
        try {
            glyphManager?.turnOff()
            currentFillPercentage = 0f
        } catch (e: Exception) {
            // Handle silently
        }
    }

    /**
     * Release resources
     */
    fun release() {
        scope.cancel()
        try {
            glyphManager?.unInit()
            isInitialized = false
        } catch (e: Exception) {
            // Handle silently
        }
    }
}
