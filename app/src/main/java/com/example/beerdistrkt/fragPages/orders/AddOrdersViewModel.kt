package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ObiectWithPrices
import kotlinx.coroutines.launch

class AddOrdersViewModel(private val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    init {
        Log.d("addOrderVM", clientID.toString())
        getClient()
    }

    private fun getClient(){
        ioScope.launch {
            val clientData = database.getObiectsWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

}
