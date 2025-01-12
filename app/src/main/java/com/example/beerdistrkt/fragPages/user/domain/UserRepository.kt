package com.example.beerdistrkt.fragPages.user.domain

import com.example.beerdistrkt.fragPages.user.data.model.AddUserRequestModel
import com.example.beerdistrkt.fragPages.user.data.model.BaseInsertApiModel
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.MutableStateFlow


interface UserRepository {
    suspend fun refreshUsers()
    suspend fun getUser(userID: String): User?
    suspend fun getUsers(): List<User>
    suspend fun getRegions(): List<WorkRegion>
    val usersFlow: MutableStateFlow<ResultState<List<User>>>
    suspend fun putUser(model: AddUserRequestModel): ResultState<BaseInsertApiModel>
    suspend fun deleteUser(userID: String): ResultState<Unit>
}