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
            },
            responseFailure = {code, error ->
                loginResponseLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                loginResponseLiveData.value = ApiResponseState.Loading(false)
            }
        )

    }

    fun setUserData(data: LoginResponse, selectedRegion: WorkRegion? = null) {
        selectedRegion?.let {
            if (it.regionID != Session.get().region?.regionID) {
                SharedPreferenceDataSource.getInstance().saveRegion(it)
                SharedPreferenceDataSource.getInstance().clearVersions()
            }
            Session.get().region = it
        }
        Session.get().justLoggedIn(data)
    }
}
