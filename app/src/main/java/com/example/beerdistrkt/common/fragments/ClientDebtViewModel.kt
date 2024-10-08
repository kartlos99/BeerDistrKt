package com.example.beerdistrkt.common.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.DebtResponse
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ClientDebtViewModel.Factory::class)
class ClientDebtViewModel @AssistedInject constructor(
    @Assisted private val clientID: Int?
) : BaseViewModel() {

    private val _clientDebtLiveData = MutableLiveData<ApiResponseState<DebtResponse?>>()
    val clientDebtLiveData: LiveData<ApiResponseState<DebtResponse?>>
        get() = _clientDebtLiveData

    init {
        if (clientID != null)
            getDebt(clientID)
        else
            _clientDebtLiveData.value = ApiResponseState.Error(null)
    }

    private fun getDebt(clientID: Int) {
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

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int?): ClientDebtViewModel
    }
}
