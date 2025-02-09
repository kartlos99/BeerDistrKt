package com.example.beerdistrkt.network

import com.example.beerdistrkt.utils.Session
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor (
    private val session: Session
) : Interceptor {

    private fun getHeadersMap(): Map<String, String> {
        return if (session.isUserLogged())
            mapOf(
                "Authorization" to "Bearer ${session.accessToken}",
                "Client" to "Android",
                "Region" to session.getRegionID()
            )
        else mapOf("Client" to "Android")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
        getHeadersMap().entries.forEach {
            newRequest.addHeader(it.key, it.value)
        }
        return chain.proceed(newRequest.build())
    }

}