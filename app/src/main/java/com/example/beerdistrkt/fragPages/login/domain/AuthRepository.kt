package com.example.beerdistrkt.fragPages.login.domain

import com.example.beerdistrkt.fragPages.login.domain.model.LoginData
import com.example.beerdistrkt.network.api.ApiResponse

interface AuthRepository {
    suspend fun signIn(username: String, password: String): ApiResponse<LoginData>
}