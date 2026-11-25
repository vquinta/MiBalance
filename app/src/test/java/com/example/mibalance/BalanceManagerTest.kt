package com.example.mibalance

import com.example.mibalance.data.BalanceManager
import org.junit.Assert.assertEquals
import org.junit.Test

class BalanceManagerTest {
    @Test
    fun centerKeepsBothChannelsAtUnityGain() {
        val (leftGain, rightGain) = BalanceManager.channelGains(0f)

        assertEquals(0.0f, leftGain, 0.0001f)
        assertEquals(0.0f, rightGain, 0.0001f)
    }

    @Test
    fun leftBalanceOnlyAttenuatesRightChannel() {
        val (leftGain, rightGain) = BalanceManager.channelGains(-0.5f)

        assertEquals(0.0f, leftGain, 0.0001f)
        assertEquals(-12.0f, rightGain, 0.0001f)
    }

    @Test
    fun rightBalanceOnlyAttenuatesLeftChannel() {
        val (leftGain, rightGain) = BalanceManager.channelGains(0.5f)

        assertEquals(-12.0f, leftGain, 0.0001f)
        assertEquals(0.0f, rightGain, 0.0001f)
    }

    @Test
    fun extremeBalanceNeverExceeds24DbAttenuation() {
        val (leftGain, rightGain) = BalanceManager.channelGains(1f)
        val (extremeLeftGain, extremeRightGain) = BalanceManager.channelGains(-1f)

        assertEquals(-24.0f, leftGain, 0.0001f)
        assertEquals(0.0f, rightGain, 0.0001f)
        assertEquals(0.0f, extremeLeftGain, 0.0001f)
        assertEquals(-24.0f, extremeRightGain, 0.0001f)
    }
}
