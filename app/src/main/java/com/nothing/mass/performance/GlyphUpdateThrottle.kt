// GlyphUpdateThrottle.kt
package com.nothing.mass.performance

import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

class GlyphUpdateThrottle(private val minIntervalMs: Long = 100) {
    private val handler = Handler(Looper.getMainLooper())
    private val isPending = AtomicBoolean(false)
    private var lastUpdate = 0L

    fun throttle(action: () -> Unit) {
        val now = System.currentTimeMillis()
        val elapsed = now - lastUpdate
        if (elapsed >= minIntervalMs && !isPending.get()) {
            lastUpdate = now
            action()
        } else if (!isPending.get()) {
            isPending.set(true)
            handler.postDelayed({
                isPending.set(false)
                lastUpdate = System.currentTimeMillis()
                action()
            }, minIntervalMs - elapsed)
        }
    }
}
