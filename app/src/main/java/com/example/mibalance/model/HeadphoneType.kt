package com.example.mibalance.model

import androidx.compose.ui.graphics.Color

enum class HeadphoneType(
    val id: String,
    val displayName: String,
    val description: String,
    val accentColor: Color
) {
    REDMI_BUDS(
        id = "redmi_buds",
        displayName = "Redmi Buds",
        description = "In-ear con almohadilla",
        accentColor = Color(0xFFFF6900)
    ),
    AIRPODS(
        id = "airpods",
        displayName = "AirPods",
        description = "Tallo largo clásico",
        accentColor = Color(0xFF007AFF)
    ),
    AIRPODS_PRO(
        id = "airpods_pro",
        displayName = "AirPods Pro",
        description = "Tallo corto in-ear",
        accentColor = Color(0xFF34C759)
    ),
    GALAXY_BUDS(
        id = "galaxy_buds",
        displayName = "Galaxy Buds",
        description = "Botón compacto",
        accentColor = Color(0xFF5856D6)
    ),
    OVER_EAR(
        id = "over_ear",
        displayName = "Diadema",
        description = "Cascos grandes",
        accentColor = Color(0xFFAF52DE)
    ),
    WIRED(
        id = "wired",
        displayName = "Con cable",
        description = "Jack / USB-C",
        accentColor = Color(0xFF00A896)
    ),
    GENERIC(
        id = "generic",
        displayName = "Otros",
        description = "Auriculares estándar",
        accentColor = Color(0xFF607D8B)
    );

    companion object {
        fun fromId(id: String?): HeadphoneType =
            entries.firstOrNull { it.id == id } ?: GENERIC
    }
}

data class AudioProfile(
    val id: String,
    val name: String,
    val headphoneType: HeadphoneType,
    val balance: Float
)
