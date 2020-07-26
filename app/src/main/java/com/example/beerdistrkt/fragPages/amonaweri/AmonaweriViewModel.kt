package com.example.beerdistrkt.fragPages.amonaweri

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ObiectWithPrices
import kotlinx.coroutines.launch

class AmonaweriViewModel(val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    init {
        getClient()
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getObiectsWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }
}
