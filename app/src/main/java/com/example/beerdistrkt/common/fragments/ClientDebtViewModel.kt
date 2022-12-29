package com.example.beerdistrkt.common.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.DebtResponse
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import com.example.beerdistrkt.utils.ApiResponseState

class ClientDebtViewModel : BaseViewModel() {

    private val _clientDebtLiveData = MutableLiveData<ApiResponseState<DebtResponse?>>()
    val clientDebtLiveData: LiveData<ApiResponseState<DebtResponse?>>
        get() = _clientDebtLiveData

    fun getDebt(clientID: Int) {
        _clientDebtLiveData.value = ApiResponseState.Loading(true)
        ApeniApiService.getInstance().getDebt(clientID).sendRequest(
            successWithData = {
                _clientDebtLiveData.value = ApiResponseState.Success(it)
            },
            failure = {
                _clientDebtLiveData.value = ApiResponseState.Error(null)
            },
            finally = {
                _clientDebtLiveData.value = ApiResponseState.Loading(false)
                if (!it)
                    _clientDebtLiveData.value = ApiResponseState.Error(null)
            },
            onConnectionFailure = {}
        )
    }
}
