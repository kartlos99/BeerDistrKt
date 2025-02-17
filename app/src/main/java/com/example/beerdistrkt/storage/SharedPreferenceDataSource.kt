package com.example.beerdistrkt.storage

import android.content.Context
import com.example.beerdistrkt.fragPages.login.domain.model.UserInfo
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SharedPreferenceDataSource(appContext: Context) {

    private val sharedPreference = appContext.getSharedPreferences("shPref", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val moshiSessionAdapter: JsonAdapter<UserInfo> = moshi.adapter(UserInfo::class.java)
    private val moshiRegionAdapter: JsonAdapter<WorkRegion> = moshi.adapter(WorkRegion::class.java)

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

    fun clearVersions() {
        sharedPreference.edit().putString(KEY_VERSION, "").apply()
    }

    fun saveLastMsgDate(text: String) {
        sharedPreference.edit().putString(MSG_DATE, text).apply()
    }

    fun getLastMsgDate(): String {
        return sharedPreference.getString(MSG_DATE, "") ?: ""
    }

    fun saveLastLogID(text: String) {
        sharedPreference.edit().putString(KEY_LOG_ID, text).apply()
    }

    fun getLastLogID(): String {
        return sharedPreference.getString(KEY_LOG_ID, "") ?: ""
    }

    fun clearSession() {
        sharedPreference.edit().putString(KEY_SESSION, "").apply()
    }

    fun saveSession(info: UserInfo) {
        val data = moshiSessionAdapter.toJson(info)
        sharedPreference.edit().putString(KEY_SESSION, data).apply()
    }

    fun getUserInfo(): UserInfo? {
        val jsonData = sharedPreference.getString(KEY_SESSION, "")
        val info = if (!jsonData.isNullOrEmpty()) moshiSessionAdapter.fromJson(jsonData) else null
        return if (info != null && info.accessToken.isNotEmpty())
            info
        else null
    }

    fun saveRegion(region: WorkRegion?) {
        val data = if (region == null) "" else moshiRegionAdapter.toJson(region)
        sharedPreference.edit().putString(KEY_REGION, data).apply()
    }

    fun getRegion(): WorkRegion? {
        val jsonData = sharedPreference.getString(KEY_REGION, "") ?: ""
        return if (jsonData.isNotEmpty()) moshiRegionAdapter.fromJson(jsonData) else null
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
        const val MSG_DATE = "msg_date"
        const val KEY_LOG_ID = "KEY_LOG_ID"
        const val KEY_SESSION = "keySession3"
        const val KEY_REGION = "keyRegion"
        const val KEY_VERSION = "vKey"
    }
}