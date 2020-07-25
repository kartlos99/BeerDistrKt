package com.example.beerdistrkt.fragPages.addEditObiects

import android.util.Log
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.network.ApeniApiService

class AddObjectViewModel(val clientID: Int) : BaseViewModel() {

    val beersLiveData = database.getBeerList()


    fun addClient(clientData: ObiectWithPrices) {

        if (clientID == 0)
            sendRequest(
                ApeniApiService.getInstance().addClient(clientData),
                successWithData = {
                    Log.d("client_scsses", it.toString())
                },
                finally = {
                    Log.d("client_final", it.toString())
                }
            )
        else updateClient(clientData)
    }

    fun updateClient(clientData: ObiectWithPrices) {
        sendRequest(
            ApeniApiService.getInstance().updateClient(clientData),
            successWithData = {
                Log.d("client_scsses", it)
            },
            finally = {
                Log.d("client_final", it.toString())
            }
        )
    }
}
