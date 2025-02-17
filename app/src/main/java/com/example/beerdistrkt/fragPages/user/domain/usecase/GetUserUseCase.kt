package com.example.beerdistrkt.fragPages.user.domain.usecase

import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userID: String) = userRepository.getUser(userID)
}