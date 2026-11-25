package com.example.mibalance.data

import android.content.Context
import android.os.Build
import com.example.mibalance.audio.DynamicsProcessingBalanceEngine
import kotlin.math.abs
import kotlin.math.roundToInt

sealed interface BalanceWriteResult {
    data class Success(val appliedBalance: Float) : BalanceWriteResult
    data class Error(val message: String) : BalanceWriteResult
}

object BalanceManager {
    private const val PREFS_NAME = "mi_balance_audio"
    private const val KEY_CURRENT_BALANCE = "current_balance"
    private const val MAX_ATTENUATION_DB = 24.0f

    private var engine: DynamicsProcessingBalanceEngine? = null

    fun normalizeBalance(raw: Float): Float {
        val clamped = raw.coerceIn(-1f, 1f)
        if (abs(clamped) < 0.01f) return 0f
        return (clamped * 100f).roundToInt() / 100f
    }

    /**
     * Convierte el balance en atenuación. El canal favorecido se mantiene a 0 dB
     * y el contrario se atenúa hasta 24 dB, sin aplicar ganancias positivas.
     */
    fun channelGains(rawBalance: Float): Pair<Float, Float> {
        val balance = normalizeBalance(rawBalance)
        return when {
            balance < 0f -> 0.0f to (balance * MAX_ATTENUATION_DB).coerceIn(-MAX_ATTENUATION_DB, 0.0f)
            balance > 0f -> (-balance * MAX_ATTENUATION_DB).coerceIn(-MAX_ATTENUATION_DB, 0.0f) to 0.0f
            else -> 0.0f to 0.0f
        }
    }

    fun getCurrentBalance(context: Context): Float {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return normalizeBalance(prefs.getFloat(KEY_CURRENT_BALANCE, 0f))
    }

    fun currentIssue(context: Context): BalanceWriteResult? =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            BalanceWriteResult.Error(UNSUPPORTED_MESSAGE)
        } else {
            null
        }

    /** Inicia la escucha de sesiones y restaura el balance guardado. */
    fun start(context: Context): BalanceWriteResult? {
        currentIssue(context)?.let { return it }
        val balance = getCurrentBalance(context)
        return obtainEngine(context).start(balance)?.let { BalanceWriteResult.Error(it) }
    }

    /** Libera todos los efectos y detiene la escucha de sesiones. */
    fun stop() {
        engine?.stop()
        engine = null
    }

    fun applyBalance(context: Context, rawValue: Float): BalanceWriteResult {
        val value = normalizeBalance(rawValue)
        currentIssue(context)?.let { return it }

        val error = obtainEngine(context).applyBalance(value)
        return if (error != null) {
            BalanceWriteResult.Error(error)
        } else {
            saveCurrentBalance(context, value)
            BalanceWriteResult.Success(value)
        }
    }

    private fun obtainEngine(context: Context): DynamicsProcessingBalanceEngine {
        return engine ?: DynamicsProcessingBalanceEngine(context.applicationContext).also {
            engine = it
        }
    }

    private fun saveCurrentBalance(context: Context, value: Float) {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putFloat(KEY_CURRENT_BALANCE, value)
            .apply()
    }

    fun formatBalance(balance: Float): String {
        val normalized = normalizeBalance(balance)
        val percent = (abs(normalized) * 100f).roundToInt()
        return when {
            percent == 0 -> "Centro (0%)"
            normalized < 0f -> "Izquierda $percent%"
            else -> "Derecha $percent%"
        }
    }

    fun formatShortBadge(balance: Float): String {
        val normalized = normalizeBalance(balance)
        val percent = (abs(normalized) * 100f).roundToInt()
        return when {
            percent == 0 -> "Centro"
            normalized < 0f -> "Izq $percent%"
            else -> "Der $percent%"
        }
    }

    private const val UNSUPPORTED_MESSAGE =
        "El balance mediante DynamicsProcessing requiere Android 9 (API 28) o superior."
}
