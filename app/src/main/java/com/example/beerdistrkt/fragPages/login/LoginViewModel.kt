package com.example.beerdistrkt.fragPages.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.LoginRequest
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.user.data.UserMapper
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userMapper: UserMapper,
    private val apeniApi: ApeniApiService,
) : BaseViewModel() {

    val loginResponseLiveData = MutableLiveData<ApiResponseState<LoginResponse>>()

    fun logIn(username: String, password: String) {
        loginResponseLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            apeniApi.logIn(LoginRequest(username, password)),
            successWithData = {
                val loginResp = LoginResponse(
                    id = it.id,
                    username = it.username,
                    name = it.name,
                    type = it.type,
                    permissions = it.permissions,
                    regions = it.regions.map { wr ->
                        userMapper.mapRegion(wr)
                    },
                    token = it.token
                )
                loginResponseLiveData.value = ApiResponseState.Success(loginResp)
                session.regions.clear()
                session.regions.addAll(loginResp.regions)
            },
            responseFailure = { code, error ->
                loginResponseLiveData.value = ApiResponseState.ApiError(code, error)
            },
            authFailure = {},
            finally = {
                loginResponseLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun setUserData(data: LoginResponse) {
        session.justLoggedIn(data)
        viewModelScope.launch {
            userPreferencesRepository.saveUserSession(session.getUserInfo())
        }
    }
}
