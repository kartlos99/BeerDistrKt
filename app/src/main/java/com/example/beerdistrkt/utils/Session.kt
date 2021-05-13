package com.example.beerdistrkt.utils

import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.storage.SharedPreferenceDataSource


data class UserInfo(
    val userID: String,
    val userType: UserType,
    val permissions: List<Permission>,
    val userName: String,
    val displayName: String,
    val accessToken: String,
    val accessTokenCreateTime: Long
)

class Session {

    var userID: String? = null
    var userType: UserType = UserType.DISTRIBUTOR
    var permissions = mutableListOf<Permission>()
    var userName: String? = null
    var displayName: String? = null
    var accessToken: String? = null
    var accessTokenCreateTime = 0L
    var regionID = "1"

    var loggedIn = false

    fun isUserLogged() = loggedIn

    fun getUserID(): Int = userID?.toInt() ?: 0

    fun justLoggedIn(userdata: LoginResponse) {
        userID = userdata.id.toString()
        userType = userdata.type
        loggedIn = true
        userName = userdata.username
        displayName = userdata.name
        accessToken = userdata.token
        accessTokenCreateTime = System.currentTimeMillis()
        permissions.clear()
        permissions.addAll(userdata.permissions)

        SharedPreferenceDataSource.getInstance().saveSession(getUserInfo())
    }

    fun clearSession(){
        userID = null
        userType = UserType.DISTRIBUTOR
        loggedIn = false
        userName = null
        displayName = null
        accessToken = null
        SharedPreferenceDataSource.getInstance().clearSession()
    }

    fun isAccessTokenValid(): Boolean {
        if (!accessToken.isNullOrEmpty())
            return System.currentTimeMillis() - accessTokenCreateTime < SESSION_DURATION
        return false
    }

    fun getUserInfo(): UserInfo {
        return UserInfo(
            userID ?: "",
            userType,
            permissions,
            userName ?: "",
            displayName ?: "",
            accessToken ?: "",
            accessTokenCreateTime
        )
    }

    fun restoreFromSavedInfo(info: UserInfo?) {
        info?.let { userdata ->
            userID = userdata.userID
            userType = userdata.userType
            permissions = userdata.permissions.toMutableList()
            loggedIn = true
            userName = userdata.userName
            displayName = userdata.displayName
            accessToken = userdata.accessToken
            accessTokenCreateTime = userdata.accessTokenCreateTime
        }
    }

    fun hasPermission(permission: Permission): Boolean {
        return permissions.contains(permission)
    }

    companion object {
        private var session: Session? = null
        const val SESSION_DURATION: Long = 60 * 60 * 1000

        @JvmStatic
        fun get(): Session {
            if (session == null) {
                session = Session()
            }
            return session!!
        }
    }
}