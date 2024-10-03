package com.example.beerdistrkt.fragPages.statement

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ObiectWithPrices
import kotlinx.coroutines.launch

class StatementViewModel(val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

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
