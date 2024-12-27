package com.example.beerdistrkt.fragPages.user.domain.usecase

import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.getUsers()

    fun usersAsFlow(): StateFlow<ResultState<List<User>>> = userRepository.usersFlow
}