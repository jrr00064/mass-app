// UsageStatsObserver.kt
package com.nothing.mass.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class UsageStatsObserver(private val context: Context) {
    private val _usageEvents = MutableSharedFlow<UsageEvent>()
    val usageEvents: SharedFlow<UsageEvent> = _usageEvents

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> onScreenOff()
                Intent.ACTION_SCREEN_ON -> onScreenOn()
                Intent.ACTION_USER_PRESENT -> onUserPresent()
            }
        }
    }

    fun startObserving() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        context.registerReceiver(screenReceiver, filter)
    }

    fun stopObserving() {
        context.unregisterReceiver(screenReceiver)
    }

    private fun onScreenOff() {
        // Trigger mass recalculation
    }

    private fun onScreenOn() {
        // Prepare for new session
    }

    private fun onUserPresent() {
        // User unlocked device
    }
}

sealed class UsageEvent {
    object ScreenOff : UsageEvent()
    object ScreenOn : UsageEvent()
    object UserPresent : UsageEvent()
}
