package com.example.mibalance.audio

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.audiofx.AudioEffect
import android.media.audiofx.DynamicsProcessing
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mibalance.data.BalanceManager

/**
 * Mantiene un DynamicsProcessing por sesión de audio. La sesión global 0 solo se
 * utiliza cuando no hay sesiones anunciadas, para evitar aplicar la atenuación dos veces.
 */
@RequiresApi(Build.VERSION_CODES.P)
class DynamicsProcessingBalanceEngine(context: Context) {
    private val appContext = context.applicationContext
    private val activeSessions = mutableSetOf<Int>()
    private val sessionEffects = mutableMapOf<Int, DynamicsProcessing>()

    private var globalEffect: DynamicsProcessing? = null
    private var currentBalance = 0f
    private var receiverRegistered = false
    private var lastError: String? = null

    private val sessionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val audioSession = intent?.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, -1) ?: -1
            if (audioSession <= 0) return

            when (intent?.action) {
                AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                    activeSessions += audioSession
                    lastError = synchronizeEffects()
                }

                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                    activeSessions -= audioSession
                    sessionEffects.remove(audioSession)?.releaseSafely()
                    lastError = synchronizeEffects()
                }
            }
        }
    }

    fun start(balance: Float): String? {
        currentBalance = balance
        registerSessionReceiver()
        lastError = synchronizeEffects()
        return lastError
    }

    fun applyBalance(balance: Float): String? {
        currentBalance = balance
        registerSessionReceiver()
        lastError = synchronizeEffects()
        return lastError
    }

    fun stop() {
        if (receiverRegistered) {
            runCatching { appContext.unregisterReceiver(sessionReceiver) }
            receiverRegistered = false
        }
        releaseAllEffects()
        activeSessions.clear()
        lastError = null
    }

    private fun synchronizeEffects(): String? {
        if (currentBalance == 0f) {
            disableBalance()
            return null
        }

        var firstError: String? = null

        if (activeSessions.isEmpty()) {
            if (globalEffect == null) {
                try {
                    globalEffect = createEffect(GLOBAL_AUDIO_SESSION)
                } catch (error: Exception) {
                    firstError = errorMessage("No se pudo activar la sesión global de audio", error)
                }
            }
        } else {
            releaseGlobalEffect()
            activeSessions.forEach { audioSession ->
                if (!sessionEffects.containsKey(audioSession)) {
                    try {
                        sessionEffects[audioSession] = createEffect(audioSession)
                    } catch (error: Exception) {
                        if (firstError == null) {
                            firstError = errorMessage(
                                "No se pudo activar la sesión de audio $audioSession",
                                error
                            )
                        }
                    }
                }
            }
        }

        globalEffect?.let { effect ->
            try {
                updateEffect(effect)
            } catch (error: Exception) {
                effect.releaseSafely()
                globalEffect = null
                if (firstError == null) {
                    firstError = errorMessage("No se pudo actualizar la sesión global de audio", error)
                }
            }
        }

        val iterator = sessionEffects.entries.iterator()
        while (iterator.hasNext()) {
            val (audioSession, effect) = iterator.next()
            try {
                updateEffect(effect)
            } catch (error: Exception) {
                effect.releaseSafely()
                iterator.remove()
                if (firstError == null) {
                    firstError = errorMessage(
                        "No se pudo actualizar la sesión de audio $audioSession",
                        error
                    )
                }
            }
        }

        return firstError
    }

    /** Al volver al centro no hace falta procesar audio: se liberan todos los efectos. */
    private fun disableBalance() {
        releaseAllEffects()
    }

    private fun createEffect(audioSession: Int): DynamicsProcessing {
        val effect = DynamicsProcessing(EFFECT_PRIORITY, audioSession, createConfig())
        try {
            updateEffect(effect)
            return effect
        } catch (error: Exception) {
            effect.releaseSafely()
            throw error
        }
    }

    private fun createConfig(): DynamicsProcessing.Config {
        val (leftGainDb, rightGainDb) = BalanceManager.channelGains(currentBalance)
        val builder = DynamicsProcessing.Config.Builder(
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
            CHANNEL_COUNT,
            false,
            0,
            false,
            0,
            false,
            0,
            false
        )
        builder.setInputGainByChannelIndex(LEFT_CHANNEL, leftGainDb)
        builder.setInputGainByChannelIndex(RIGHT_CHANNEL, rightGainDb)

        return builder.build().also { config ->
            for (channel in 0 until CHANNEL_COUNT) {
                config.getPreEqByChannelIndex(channel).setEnabled(false)
                config.getMbcByChannelIndex(channel).setEnabled(false)
                config.getPostEqByChannelIndex(channel).setEnabled(false)
                config.getLimiterByChannelIndex(channel).setEnabled(false)
            }
        }
    }

    private fun updateEffect(effect: DynamicsProcessing) {
        if (!effect.enabled) {
            val result = effect.setEnabled(true)
            if (result != AudioEffect.SUCCESS) {
                throw IllegalStateException("DynamicsProcessing no pudo activarse (código $result).")
            }
        }
        val (leftGainDb, rightGainDb) = BalanceManager.channelGains(currentBalance)
        effect.setInputGainbyChannel(LEFT_CHANNEL, leftGainDb)
        effect.setInputGainbyChannel(RIGHT_CHANNEL, rightGainDb)
    }

    private fun releaseGlobalEffect() {
        globalEffect?.releaseSafely()
        globalEffect = null
    }

    private fun releaseAllEffects() {
        releaseGlobalEffect()
        sessionEffects.values.forEach { it.releaseSafely() }
        sessionEffects.clear()
    }

    private fun DynamicsProcessing.releaseSafely() {
        runCatching { release() }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerSessionReceiver() {
        if (receiverRegistered) return

        val filter = IntentFilter().apply {
            addAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
            addAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.registerReceiver(sessionReceiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            appContext.registerReceiver(sessionReceiver, filter)
        }
        receiverRegistered = true
    }

    private fun errorMessage(prefix: String, error: Exception): String {
        val detail = error.localizedMessage?.takeIf { it.isNotBlank() }
            ?: error.javaClass.simpleName
        return "$prefix: $detail"
    }

    private companion object {
        const val CHANNEL_COUNT = 2
        const val LEFT_CHANNEL = 0
        const val RIGHT_CHANNEL = 1
        const val GLOBAL_AUDIO_SESSION = 0
        const val EFFECT_PRIORITY = 0
    }
}
