package com.example.beerdistrkt.fragPages.addEditObiects

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddObjectViewModel(val clientID: Int) : BaseViewModel() {

    val beersLiveData = database.getBeerList()
    var clientObject: ObiectWithPrices? = null
    val clientObjectLiveData = MutableLiveData<ObiectWithPrices?>()
    val clientSaveMutableLiveData = MutableLiveData<ApiResponseState<ObiectWithPrices?>>()

    init {
        Log.d("ID", "clientID $clientID")
        if (clientID > 0) {
            ioScope.launch {
                clientObject = database.getObiectsWithPrices(clientID)
                delay(50)
                uiScope.launch {
                    clientObjectLiveData.value = clientObject
                }
            }
        }
    }

    fun addClient(clientData: ObiectWithPrices) {

        if (clientID == 0) {
            clientSaveMutableLiveData.value = ApiResponseState.Loading(true)
            sendRequest(
                ApeniApiService.getInstance().addClient(clientData),
                successWithData = {
                    val insertedClientID = it.toInt()
                    clientData.obieqti.id = insertedClientID
                    val prices = clientData.prices.map { prItem ->
                        prItem.copy(objID = insertedClientID)
                    }
                    saveToLocalDB(ObiectWithPrices(clientData.obieqti, prices))
                    clientSaveMutableLiveData.value = ApiResponseState.Success(clientData)
                },
                responseFailure = { code, error ->
                    clientSaveMutableLiveData.value = ApiResponseState.ApiError(code, error)
                },
                finally = {
                    clientSaveMutableLiveData.value = ApiResponseState.Loading(false)
                }
            )
        } else updateClient(clientData)
    }

    fun updateClient(clientData: ObiectWithPrices) {
        clientSaveMutableLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().updateClient(clientData),
            successWithData = {
                saveToLocalDB(clientData)
                clientSaveMutableLiveData.value = ApiResponseState.Success(clientData)
            },
            responseFailure = { code, error ->
                clientSaveMutableLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                clientSaveMutableLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun saveToLocalDB(clientData: ObiectWithPrices) {
        ioScope.launch {
            database.insertObiecti(clientData.obieqti)
            clientData.prices.forEach { beerPrice ->
                database.insertBeerPrice(beerPrice)
            }
        }
    }
}
