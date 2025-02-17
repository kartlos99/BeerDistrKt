package com.example.beerdistrkt.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class UserAuthenticator(
    private val authHelper: AuthHelper
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        synchronized(this) {
            if (authHelper.token != null)
                authHelper.logout()
        }
        return null
    }
}