package com.example.safelugg.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "safelugg_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setUserLoggedIn(context: Context, isLoggedIn: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearUserLoggedIn(context: Context) {
        getPrefs(context).edit().remove(KEY_IS_LOGGED_IN).apply()
    }

    fun setUserId(context: Context, userId: Long){
        getPrefs(context).edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): Long{
        return getPrefs(context).getLong(KEY_USER_ID, -1L)
    }

    private const val KEY_USER_EMAIL = "user_email"

    fun setUserEmail(context: Context, email: String) {
        getPrefs(context).edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, null)
    }

}
