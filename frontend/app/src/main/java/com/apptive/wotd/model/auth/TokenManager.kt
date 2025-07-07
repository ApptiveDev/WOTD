package com.apptive.wotd.model.auth

import android.content.Context
import androidx.browser.trusted.sharing.ShareTarget.FileFormField.KEY_NAME
import androidx.core.content.edit

object TokenManager {
    private const val PREF_NAME = "user"
    private const val KEY_ACCESS = "accessToken"

    fun saveData(context: Context, accessToken: String, name: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(KEY_ACCESS, accessToken)
            putString(KEY_NAME, name)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ACCESS, null)

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit { clear() }
    }
}