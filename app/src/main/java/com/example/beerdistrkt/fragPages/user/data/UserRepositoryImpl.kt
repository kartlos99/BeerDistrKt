package com.example.beerdistrkt.fragPages.user.data

import com.example.beerdistrkt.fragPages.user.data.model.AddUserRequestModel
import com.example.beerdistrkt.fragPages.user.data.model.BaseInsertApiModel
import com.example.beerdistrkt.fragPages.user.data.model.DeleteRecordApiModel
import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.example.beerdistrkt.fragPages.user.domain.usecase.filterByRegion
import com.example.beerdistrkt.network.api.ApiResponse
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
    private var regions: List<WorkRegion> = emptyList()

    override val usersFlow: MutableStateFlow<ResultState<List<User>>> =
        MutableStateFlow(ResultState.Success(emptyList()))

    override suspend fun getUsers(regionId: Int): List<User> {
        if (users.isEmpty())
            fetchUsers()
        return users
            .filterByRegion(regionId)
            .ifEmpty {
                fetchUsers()
                users.filterByRegion(regionId)
            }
    }

    override suspend fun getRegions(): List<WorkRegion> {
        return regions
    }

    override suspend fun getUser(userID: String): User? {
        val user = users.find { it.id == userID }
        return if (user == null) {
            fetchUsers()
            users.find { it.id == userID }
        } else {
            user
        }
    }

    override suspend fun refreshUsers() {
        fetchUsers()
    }

    private suspend fun fetchUsers() {
        usersFlow.emit(ResultState.Loading)
        apiCall {
            val usersDto = api.getUsers()
            regions = usersDto.regions.map(userMapper::mapRegion)
            userMapper.toDomain(usersDto)
                .also { users = it }
        }.also {
            usersFlow.emit(it.toResultState())
        }
    }

    override suspend fun putUser(model: AddUserRequestModel): ApiResponse<BaseInsertApiModel> {
        return apiCall {
            api.putUser(model)
        }.also {
            if (it is ApiResponse.Success) {
                fetchUsers()
            }
        }
    }

    override suspend fun deleteUser(userID: String): ResultState<Unit> {
        return apiCall {
            api.deleteRecord(
                DeleteRecordApiModel(
                    recordID = userID,
                    table = USERS_TABLE
                )
            )
        }.also {
            fetchUsers()
        }.toResultState()
    }

    companion object {
        private const val USERS_TABLE = "users"
    }
}