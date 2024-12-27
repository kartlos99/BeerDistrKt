package com.example.beerdistrkt.fragPages.user.data

import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class UserRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val userMapper: UserMapper,
    ioDispatcher: CoroutineDispatcher,
) : BaseRepository(ioDispatcher), UserRepository {

    private var users: List<User> = emptyList()

    override val usersFlow: MutableStateFlow<ResultState<List<User>>> =
        MutableStateFlow(ResultState.Success(emptyList()))

    override suspend fun getUsers(): List<User> {
        return users.ifEmpty {
            fetchUsers()
            users
        }
    }

    override suspend fun getUser(): User {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUsers() {
        fetchUsers()
    }

    private suspend fun fetchUsers() {
        usersFlow.emit(ResultState.Loading)
        apiCall {
            val usersDto = api.getUsers()
            userMapper.toDomain(usersDto)
                .also { users = it }
        }.also {
            usersFlow.emit(it.toResultState())
        }
    }
}