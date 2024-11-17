package com.example.beerdistrkt.fragPages.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.LoginRequest
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel() {

    val loginResponseLiveData = MutableLiveData<ApiResponseState<LoginResponse>>()

    fun logIn(username: String, password: String){
        loginResponseLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().logIn(LoginRequest(username, password)),
            successWithData = {
                loginResponseLiveData.value = ApiResponseState.Success(it)
                Session.get().regions.clear()
                Session.get().regions.addAll(it.regions)
            },
            responseFailure = {code, error ->
                loginResponseLiveData.value = ApiResponseState.ApiError(code, error)
            },
            authFailure = {},
            finally = {
                loginResponseLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun setUserData(data: LoginResponse) {
        Session.get().justLoggedIn(data)
        viewModelScope.launch {
            userPreferencesRepository.saveUserSession(Session.get().getUserInfo())
        }
    }
}
