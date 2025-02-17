package com.example.beerdistrkt.network

import com.example.beerdistrkt.utils.LogoutUtil

class AuthHelper(
    private val logoutUtil: LogoutUtil,
) {
    fun logout() {
        logoutUtil.logout()
    }

    val token: String?
        get() = logoutUtil.token
}