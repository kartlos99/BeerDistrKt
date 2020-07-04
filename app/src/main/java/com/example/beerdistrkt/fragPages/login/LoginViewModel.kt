package com.example.beerdistrkt.fragPages.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.LoginRequest
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session

class LoginViewModel : BaseViewModel() {

    val loginResponseLiveData = MutableLiveData<ApiResponseState<LoginResponse>>()

    fun logIn(username: String, password: String){
        loginResponseLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().logIn(LoginRequest(username, password)),
            successWithData = {
                Log.d("user", it.toString())
                Session.get().justLoggedIn(it)
                loginResponseLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = {code, error ->
                loginResponseLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                loginResponseLiveData.value = ApiResponseState.Loading(false)
            }
        )

    }
}
