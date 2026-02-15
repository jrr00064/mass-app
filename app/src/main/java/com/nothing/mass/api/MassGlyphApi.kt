// MassGlyphApi.kt
package com.nothing.mass.api

import android.content.Context
import com.nothing.mass.core.MassData
import com.nothing.mass.core.MassCalculator
import com.nothing.mass.glyph.GlyphController
import kotlinx.coroutines.flow.StateFlow

interface MassGlyphApi {
    fun observeMassData(): StateFlow<MassData>
    suspend fun recalculateMass()
    suspend fun setDailyLimit(limitMinutes: Float)
    suspend fun forceGlyphUpdate()
    fun clearGlyph()
}

class MassGlyphApiImpl(
    private val context: Context,
    private val massCalculator: MassCalculator,
    private val glyphController: GlyphController
) : MassGlyphApi {
    override fun observeMassData(): StateFlow<MassData> {
        return massCalculator.massData
    }

    override suspend fun recalculateMass() {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000)
        massCalculator.calculateMass(startTime, endTime)
    }

    override suspend fun setDailyLimit(limitMinutes: Float) {
        // Update calculator with new limit and recalculate
    }

    override suspend fun forceGlyphUpdate() {
        val currentData = massCalculator.massData.value
        // Trigger immediate glyph refresh
    }

    override fun clearGlyph() {
        glyphController.clear()
    }
}
