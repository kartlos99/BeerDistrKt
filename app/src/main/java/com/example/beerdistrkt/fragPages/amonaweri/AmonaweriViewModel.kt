package com.example.beerdistrkt.fragPages.amonaweri

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.DataResponse
import com.example.beerdistrkt.models.DebtResponse
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.launch

class AmonaweriViewModel(val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val getDebtLiveData = MutableLiveData<ApiResponseState<DebtResponse>>()

    init {
        getClient()
        getDebt()
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getObiectsWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

    private fun getDebt() {
        sendRequest(
            ApeniApiService.getInstance().getDebt(clientID),
            successWithData = {
                getDebtLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = {code, error ->
                if (code == DataResponse.ErrorCodeDataIsNull)
                    getDebtLiveData.value = ApiResponseState.Success(
                        DebtResponse(clientID, "", .0, 0, .0, 0, 0, 0, listOf())
                    )
            }
        )
    }
}
