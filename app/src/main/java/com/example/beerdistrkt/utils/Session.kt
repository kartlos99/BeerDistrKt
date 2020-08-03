package com.example.beerdistrkt.utils

import android.content.Context
import com.example.beerdistrkt.fragPages.login.models.LoginResponse

enum class UserType {
    ADMIN, DISTRIBUTOR
}

class Session {

    var userID: String? = null
    var userType: UserType = UserType.DISTRIBUTOR
    var userName: String? = null
    var displayName: String? = null

    var loggedIn = false

    fun isUserLogged() = loggedIn

    fun getUserID(): Int = userID?.toInt() ?: 0

    fun justLoggedIn(userdata: LoginResponse) {
        userID = userdata.id.toString()
        userType = if (userdata.type == 2) UserType.ADMIN else UserType.DISTRIBUTOR
        loggedIn = true
        userName = userdata.username
        displayName = userdata.name
    }

    fun clearSession(){
        userID = null
        userType = UserType.DISTRIBUTOR
        loggedIn = false
        userName = null
        displayName = null
    }

    companion object {
        private var session: Session? = null

        @JvmStatic
        fun get(): Session {
            if (session == null) {
                session = Session()
            }
            return session!!
        }
    }
}