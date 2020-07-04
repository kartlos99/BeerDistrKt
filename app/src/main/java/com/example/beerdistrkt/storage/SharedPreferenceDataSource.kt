package com.example.beerdistrkt.storage

import android.annotation.SuppressLint
import android.content.Context
import com.example.beerdistrkt.models.VcsResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SharedPreferenceDataSource(appContext: Context) {

    private val sharedPreference = appContext.getSharedPreferences("shPref", Context.MODE_PRIVATE)

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val moshiJsonAdapter: JsonAdapter<VcsResponse> = moshi.adapter(VcsResponse::class.java)

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

    fun saveVersions(version: VcsResponse) {
        val data = moshiJsonAdapter.toJson(version)
        sharedPreference.edit().putString("vKey", data).apply()
    }

    fun getVersions(): VcsResponse? {
        val jsonData = sharedPreference.getString("vKey", "") ?: ""
        return if (jsonData.isNotEmpty()) moshiJsonAdapter.fromJson(jsonData) else null
    }

    companion object {
        private var instance: SharedPreferenceDataSource? = null

        fun initialize(context: Context) {
            if (instance == null) {
                instance = SharedPreferenceDataSource(context)
            }
        }

        fun getInstance(): SharedPreferenceDataSource {
            return instance!!
        }

        const val USERNAME = "username"
        const val PASS = "pass"
    }
}