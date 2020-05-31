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
}