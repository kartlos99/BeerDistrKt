package com.example.beerdistrkt.fragPages.user.domain.usecase

import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val session: Session,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        regionId: Int = session.region?.id ?: 0
    ): List<User> = userRepository.getUsers(regionId).sortedBy { it.username }

    fun usersAsFlow(): StateFlow<ResultState<List<User>>> = userRepository.usersFlow
}

fun List<User>.filterByRegion(regionId: Int) = this.filter { user ->
    user.regions.map { it.id }.contains(regionId)
}