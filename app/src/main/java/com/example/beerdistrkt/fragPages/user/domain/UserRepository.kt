package com.example.beerdistrkt.fragPages.user.domain

import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.MutableStateFlow


interface UserRepository {
    suspend fun refreshUsers()
    suspend fun getUser(): User
    suspend fun getUsers(): List<User>
    val usersFlow: MutableStateFlow<ResultState<List<User>>>
}