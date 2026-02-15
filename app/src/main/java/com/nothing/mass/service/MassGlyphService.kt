// MassGlyphService.kt
package com.nothing.mass.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.nothing.mass.core.MassCalculator
import com.nothing.mass.glyph.GlyphController
import kotlinx.coroutines.*

class MassGlyphService : Service() {
    private lateinit var massCalculator: MassCalculator
    private lateinit var glyphController: GlyphController
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        massCalculator = MassCalculator(applicationContext)
        glyphController = GlyphController(applicationContext)

        if (!glyphController.initialize()) {
            stopSelf()
            return
        }

        glyphController.observeMassData(massCalculator.massData)
        massCalculator.startTracking(intervalMinutes = 5)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        massCalculator.stopTracking()
        glyphController.clear()
        glyphController.release()
        serviceScope.cancel()
    }
}
