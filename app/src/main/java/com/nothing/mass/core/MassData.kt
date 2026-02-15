// MassDataModel.kt
package com.nothing.mass.core

import kotlinx.coroutines.flow.StateFlow

/**
 * Core data model representing the current mass state
 */
data class MassData(
    val currentMass: Float,
    val dailyLimit: Float = 480f,
    val state: MassState,
    val percentageFilled: Float,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isAtLimit: Boolean
        get() = percentageFilled >= 1.0f

    companion object {
        fun empty() = MassData(
            currentMass = 0f,
            dailyLimit = 480f,
            state = MassState.LIGHT,
            percentageFilled = 0f
        )
    }
}

enum class MassState(
    val threshold: Float,
    val brightness: Float,
    val pulseEnabled: Boolean
) {
    LIGHT(0.0f, 0.3f, false),
    DENSE(0.6f, 0.6f, false),
    CRITICAL(0.9f, 1.0f, true);

    companion object {
        fun fromPercentage(percentage: Float): MassState {
            return when {
                percentage >= CRITICAL.threshold -> CRITICAL
                percentage >= DENSE.threshold -> DENSE
                else -> LIGHT
            }
        }
    }
}

enum class AppCategory(val weight: Float) {
    SOCIAL(1.5f),
    ENTERTAINMENT(1.2f),
    GAMING(1.3f),
    PRODUCTIVITY(0.6f),
    UTILITIES(0.4f),
    HEALTH(0.3f),
    EDUCATION(0.5f),
    COMMUNICATION(0.8f),
    SHOPPING(1.1f),
    NEWS(0.9f),
    UNDEFINED(1.0f)
}
