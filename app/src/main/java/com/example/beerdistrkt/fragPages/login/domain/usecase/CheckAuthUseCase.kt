package com.example.beerdistrkt.fragPages.login.domain.usecase

import com.example.beerdistrkt.fragPages.login.domain.AuthRepository
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject


class CheckAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): ApiResponse<Any> =
        authRepository.checkAuth()
}