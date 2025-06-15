package com.example.beerdistrkt.fragPages.login.data

import com.example.beerdistrkt.fragPages.login.domain.AuthRepository
import com.example.beerdistrkt.fragPages.login.domain.model.LoginData
import com.example.beerdistrkt.fragPages.login.data.model.LoginRequest
import com.example.beerdistrkt.fragPages.user.data.UserMapper
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class AuthRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val userMapper: UserMapper,
    private val session: Session,
    ioDispatcher: CoroutineDispatcher,
) : BaseRepository(ioDispatcher), AuthRepository {

    override suspend fun signIn(username: String, password: String): ApiResponse<LoginData> {
        return apiCall {
            val userData = api.signIn(
                LoginRequest(
                    username = username,
                    password = password
                )
            )
            LoginData(
                id = userData.id,
                username = userData.username,
                name = userData.name,
                type = userData.type,
                permissions = userData.permissions,
                regions = userData.regions.map { wr ->
                    userMapper.mapRegion(wr)
                },
                token = userData.token
            ).also {

                session.justLoggedIn(it)
            }
        }
    }
}