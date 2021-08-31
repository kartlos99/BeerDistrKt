package com.example.beerdistrkt.fragPages.login

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.LoginRequest
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session

class LoginViewModel : BaseViewModel() {

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
    }
}
