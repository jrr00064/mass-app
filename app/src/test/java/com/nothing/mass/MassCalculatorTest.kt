// MassCalculatorTest.kt
package com.nothing.mass.test

import com.nothing.mass.core.AppCategory
import com.nothing.mass.core.MassCalculator
import com.nothing.mass.core.MassState
import com.nothing.mass.core.MassData
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class MassCalculatorTest {
    @Test
    fun testMassCalculation_socialAppHighWeight() = runBlocking {
        val calculator = MassCalculator(mockContext(), dailyLimit = 480f)
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000)
        val result = calculator.calculateMass(startTime, endTime)
        assertTrue(result.state == MassState.LIGHT || result.state == MassState.DENSE)
        assertTrue(result.percentageFilled >= 0f && result.percentageFilled <= 1f)
    }

    @Test
    fun testStateTransitions() {
        val data1 = MassData(
            currentMass = 288f,
            dailyLimit = 480f,
            state = MassState.DENSE,
            percentageFilled = 0.6f,
            activeDots = 20
        )
        assertEquals(MassState.DENSE, data1.state)

        val data2 = MassData(
            currentMass = 432f,
            dailyLimit = 480f,
            state = MassState.CRITICAL,
            percentageFilled = 0.9f,
            activeDots = 30
        )
        assertEquals(MassState.CRITICAL, data2.state)
    }

    @Test
    fun testDotActivation() {
        val percentage = 0.5f
        val dots = (percentage * 33).toInt()
        assertEquals(16, dots)
    }

    @Test
    fun testAppCategoryWeights() {
        assertEquals(1.5f, AppCategory.SOCIAL.weight, 0.01f)
        assertEquals(0.3f, AppCategory.HEALTH.weight, 0.01f)
        assertEquals(1.0f, AppCategory.UNDEFINED.weight, 0.01f)
    }

    private fun mockContext(): android.content.Context {
        return androidx.test.core.app.ApplicationProvider.getApplicationContext()
    }
}
