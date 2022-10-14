package com.example.beerdistrkt.fragPages.amonaweri

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.DebtResponse
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.launch

class AmonaweriViewModel(val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val getDebtLiveData = MutableLiveData<ApiResponseState<DebtResponse>>()

    init {
        getClient()
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getCustomerWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

}
