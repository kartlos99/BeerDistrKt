package com.example.beerdistrkt.storage

import android.annotation.SuppressLint
import android.content.Context

class SharedPreferenceDataSource(appContext: Context) {

    private val sharedPreference = appContext.getSharedPreferences("shPref", Context.MODE_PRIVATE)

    @SuppressLint("ApplySharedPref")
    fun saveSession(sessionToken: String) {
        sharedPreference.edit().putString("accessToken", sessionToken).commit()
//        sharedPreference.edit().putString("refreshToken", session.refreshToken).commit()
    }

    fun clearSession() {
        sharedPreference.edit().putString("accessToken", null).apply()
//        sharedPreferences.edit().putString("refreshToken", null).apply()
    }

    fun getSession(): String {
        return sharedPreference.getString("accessToken", "") ?: ""
    }

    fun saveUserName(username: String) {
        sharedPreference.edit().putString(USERNAME, username).apply()
    }

    fun getUserName(): String {
        return sharedPreference.getString(USERNAME, "") ?: ""
    }

    fun savePassword(password: String) {
        sharedPreference.edit().putString(PASS, password).apply()
    }

    fun getPass(): String {
        return sharedPreference.getString(PASS, "") ?: ""
    }


    companion object {
        private var instance: SharedPreferenceDataSource? = null

        fun get(appContext: Context): SharedPreferenceDataSource {
            if (instance == null) {
                instance = SharedPreferenceDataSource(appContext)
            }
            return instance!!
        }

        const val USERNAME = "username"
        const val PASS = "pass"
    }
}