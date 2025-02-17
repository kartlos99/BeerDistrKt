package com.example.beerdistrkt.fragPages.user.domain.usecase

import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import javax.inject.Inject

class RefreshUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.refreshUsers()
}