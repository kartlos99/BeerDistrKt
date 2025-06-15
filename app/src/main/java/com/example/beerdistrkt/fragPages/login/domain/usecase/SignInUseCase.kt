package com.example.beerdistrkt.fragPages.login.domain.usecase

import com.example.beerdistrkt.fragPages.login.domain.AuthRepository
import com.example.beerdistrkt.fragPages.login.domain.model.LoginData
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject


class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): ApiResponse<LoginData> =
        authRepository.signIn(
            username = username,
            password = password,
        )
}