package com.mibotiquin.di

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("mibotiquin_prefs", Context.MODE_PRIVATE)

    var activeCabinetId: String?
        get() = prefs.getString(KEY_ACTIVE_CABINET, null)
        set(value) {
            prefs.edit().putString(KEY_ACTIVE_CABINET, value).apply()
        }

    companion object {
        private const val KEY_ACTIVE_CABINET = "active_cabinet_id"
    }
}
