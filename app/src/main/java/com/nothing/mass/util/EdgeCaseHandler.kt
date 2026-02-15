// EdgeCaseHandler.kt
package com.nothing.mass.util

import android.content.Context
import com.nothing.mass.core.MassData
import com.nothing.mass.core.MassState

object EdgeCaseHandler {
    fun handleSystemRestart(context: Context): MassData? {
        val prefs = context.getSharedPreferences("mass_state", Context.MODE_PRIVATE)
        return if (prefs.contains("last_mass")) {
            MassData(
                currentMass = prefs.getFloat("last_mass", 0f),
                dailyLimit = prefs.getFloat("daily_limit", 480f),
                state = MassState.valueOf(prefs.getString("state", "LIGHT")!!),
                percentageFilled = prefs.getFloat("percentage", 0f),
                activeDots = prefs.getInt("active_dots", 0),
                timestamp = prefs.getLong("timestamp", System.currentTimeMillis())
            )
        } else {
            null
        }
    }

    fun persistState(context: Context, massData: MassData) {
        context.getSharedPreferences("mass_state", Context.MODE_PRIVATE)
            .edit()
            .putFloat("last_mass", massData.currentMass)
            .putFloat("daily_limit", massData.dailyLimit)
            .putString("state", massData.state.name)
            .putFloat("percentage", massData.percentageFilled)
            .putInt("active_dots", massData.activeDots)
            .putLong("timestamp", massData.timestamp)
            .apply()
    }

    fun handleAppUninstalled(packageName: String) {
        // Remove from internal tracking if needed
    }

    fun handleMidnightReset(context: Context) {
        val prefs = context.getSharedPreferences("mass_state", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
