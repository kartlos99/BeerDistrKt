package com.example.beerdistrkt.utils

import android.content.Context

enum class UserType {
    ADMIN, DISTRIBUTOR
}

class Session {

    var userID: String? = "15"
    val userType: UserType = UserType.ADMIN

    fun getUserID(): Int = userID?.toInt() ?: 0

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