// MassCalculator.kt
package com.nothing.mass.core

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.exp
import kotlin.math.min

class MassCalculator(
    private val context: Context,
    private val dailyLimit: Float = 480f
) {
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager
    private val _massData = MutableStateFlow(MassData.empty())
    val massData: StateFlow<MassData> = _massData
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun calculateMass(startTime: Long, endTime: Long): MassData {
        return withContext(Dispatchers.Default) {
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )
            var totalMass = 0f
            val now = System.currentTimeMillis()
            stats?.forEach { stat ->
                if (stat.totalTimeInForeground > 0) {
                    val category = getAppCategory(stat.packageName)
                    val usageMinutes = stat.totalTimeInForeground / 60000f
                    val recencyFactor = calculateRecencyFactor(stat.lastTimeUsed, now)
                    totalMass += usageMinutes * category.weight * recencyFactor
                }
            }
            val percentage = min(totalMass / dailyLimit, 1.0f)
            val state = MassState.fromPercentage(percentage)
            // For 25x25 matrix (625 LEDs), we use percentage directly
            // The visual fill is handled by the GlyphController creating a circular fill pattern
            MassData(
                currentMass = totalMass,
                dailyLimit = dailyLimit,
                state = state,
                percentageFilled = percentage
            )
        }
    }

    private fun calculateRecencyFactor(lastUsed: Long, now: Long): Float {
        val hoursSince = (now - lastUsed) / 3600000f
        return 0.5f + 0.5f * exp(-hoursSince / 24f)
    }

    private fun getAppCategory(packageName: String): AppCategory {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val category = appInfo.category
            when (category) {
                ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.SOCIAL
                ApplicationInfo.CATEGORY_GAME -> AppCategory.GAMING
                ApplicationInfo.CATEGORY_AUDIO, ApplicationInfo.CATEGORY_VIDEO -> AppCategory.ENTERTAINMENT
                ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.PRODUCTIVITY
                ApplicationInfo.CATEGORY_NEWS -> AppCategory.NEWS
                ApplicationInfo.CATEGORY_MAPS -> AppCategory.UTILITIES
                else -> categorizeByPackageName(packageName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            AppCategory.UNDEFINED
        }
    }

    private fun categorizeByPackageName(packageName: String): AppCategory {
        return when {
            packageName.contains("instagram") || packageName.contains("facebook") ||
            packageName.contains("twitter") || packageName.contains("tiktok") -> AppCategory.SOCIAL
            packageName.contains("youtube") || packageName.contains("netflix") ||
            packageName.contains("spotify") -> AppCategory.ENTERTAINMENT
            packageName.contains("gmail") || packageName.contains("slack") ||
            packageName.contains("teams") -> AppCategory.COMMUNICATION
            packageName.contains("fitness") || packageName.contains("health") -> AppCategory.HEALTH
            else -> AppCategory.UNDEFINED
        }
    }

    fun startTracking(intervalMinutes: Long = 5) {
        scope.launch {
            while (isActive) {
                val endTime = System.currentTimeMillis()
                val startTime = endTime - (24 * 60 * 60 * 1000)
                val newMassData = calculateMass(startTime, endTime)
                _massData.value = newMassData
                delay(intervalMinutes * 60 * 1000)
            }
        }
    }

    fun stopTracking() {
        scope.cancel()
    }

}
