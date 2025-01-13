package com.example.beerdistrkt.utils

import com.example.beerdistrkt.fragPages.login.domain.model.UserInfo
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Session @Inject constructor() {

    var userID: String? = null
    var userType: UserType = UserType.DISTRIBUTOR
    private var permissions = mutableListOf<Permission>()
    var userName: String? = null
    var displayName: String? = null
    var accessToken: String? = null
    private var accessTokenCreateTime = 0L
    var region: WorkRegion? = null
    var regions: MutableList<WorkRegion> = mutableListOf()

    var loggedIn = false

    fun isUserLogged() = loggedIn

    fun getUserID(): Int = userID?.toInt() ?: 0

    fun getRegionID(): String = (region?.id ?: 0).toString()

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
        regions = userdata.regions.toMutableList()
    }

    fun clearSession() {
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

    fun isLogOutDone(): Boolean = userName.isNullOrEmpty()

    fun getUserInfo(): UserInfo {
        return UserInfo(
            userID ?: "",
            userType,
            permissions,
            userName ?: "",
            displayName ?: "",
            accessToken ?: "",
            accessTokenCreateTime,
            regions
        )
    }

    fun restoreLastRegion(lastRegion: WorkRegion?) {
        region = lastRegion
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
            regions = userdata.regions.toMutableList()
        }
    }

    fun hasPermission(permission: Permission): Boolean {
        return permissions.contains(permission)
    }

    fun saveSession() {
//        SharedPreferenceDataSource.getInstance().saveSession(getUserInfo())
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