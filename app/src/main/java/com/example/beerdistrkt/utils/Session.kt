package com.example.beerdistrkt.utils

import com.example.beerdistrkt.fragPages.login.domain.model.UserInfo
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion


class Session(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    var userID: String? = null
    var userType: UserType = UserType.DISTRIBUTOR
    private var permissions = mutableListOf<Permission>()
    var userName: String? = null
    var displayName: String? = null
    var accessToken: String? = null
    private var accessTokenCreateTime = 0L
    var region: WorkRegion? = null
    var regions: MutableList<WorkRegion> = mutableListOf()
    val regionId: Int
        get() = region?.id ?: 0

    var loggedIn = false

    fun isUserLogged() = loggedIn && accessToken != null

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
        permissions.clear()
    }

    suspend fun clearUserPreference() {
        userPreferencesRepository.clearSession()
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

    companion object {
        const val SESSION_DURATION: Long = 60 * 60 * 1000
    }
}