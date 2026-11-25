package com.example.mibalance.data

import android.content.Context
import com.example.mibalance.model.AudioProfile
import com.example.mibalance.model.HeadphoneType
import org.json.JSONArray
import org.json.JSONObject

class ProfileRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadProfiles(): List<AudioProfile> {
        val raw = prefs.getString(KEY_PROFILES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        AudioProfile(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            headphoneType = HeadphoneType.fromId(obj.optString("headphoneType")),
                            balance = BalanceManager.normalizeBalance(
                                obj.optDouble("balance", 0.0).toFloat()
                            )
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun saveProfiles(profiles: List<AudioProfile>) {
        val array = JSONArray()
        profiles.forEach { profile ->
            val obj = JSONObject().apply {
                put("id", profile.id)
                put("name", profile.name)
                put("headphoneType", profile.headphoneType.id)
                put("balance", profile.balance.toDouble())
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PROFILES, array.toString()).apply()
    }

    fun loadActiveProfileId(): String? = prefs.getString(KEY_ACTIVE_PROFILE_ID, null)

    fun saveActiveProfileId(id: String?) {
        prefs.edit().apply {
            if (id == null) remove(KEY_ACTIVE_PROFILE_ID) else putString(KEY_ACTIVE_PROFILE_ID, id)
        }.apply()
    }

    private companion object {
        const val PREFS_NAME = "mi_balance_prefs"
        const val KEY_PROFILES = "profiles_json"
        const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
    }
}
