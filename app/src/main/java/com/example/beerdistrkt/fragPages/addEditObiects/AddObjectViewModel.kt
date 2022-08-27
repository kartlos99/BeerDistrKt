package com.example.beerdistrkt.fragPages.addEditObiects

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.models.AttachRegionsRequest
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.repos.ApeniRepo
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddObjectViewModel(val clientID: Int) : BaseViewModel() {

    val beersLiveData = database.getBeerList()
    var clientObject: ObiectWithPrices? = null
    val clientObjectLiveData = MutableLiveData<ObiectWithPrices?>()
    val clientSaveMutableLiveData = MutableLiveData<ApiResponseState<ObiectWithPrices?>>()
    val clientRegionsLiveData = MutableLiveData<ApiResponseState<List<AttachedRegion>>>()
    val regions = mutableListOf<AttachedRegion>()
    val selectedRegions = mutableListOf<AttachedRegion>()

    private val repository = ApeniRepo()

    init {
        beersLiveData.observeForever {
            if (clientID > 0) {
                repository.getCustomerData(clientID).observeForever { customerData ->
                    clientObject = customerData
                    uiScope.launch {
                        delay(50)
                        clientObjectLiveData.value = clientObject
                    }
                }
            }
        }
    }

    fun addClient(clientData: ObiectWithPrices) {
        if (callIsBlocked) return
        callIsBlocked = true

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

    private fun updateClient(clientData: ObiectWithPrices) {
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

    private fun saveToLocalDB(clientData: ObiectWithPrices) {
        ioScope.launch {
            database.insertObiecti(clientData.obieqti)
            clientData.prices.forEach { beerPrice ->
                database.insertBeerPrice(beerPrice)
            }
        }
    }

    fun getRegionForClient() {
        sendRequest(
            ApeniApiService.getInstance().getAttachedRegions(clientID),
            successWithData = {
                regions.clear()
                regions.addAll(it)
                clientRegionsLiveData.value =
                    ApiResponseState.Success(regions.filter { r -> r.isAttached })
            }
        )
    }

    fun getAllRegionNames(): Array<String> {
        return regions.map { it.name }.toTypedArray()
    }

    fun getSelectedRegions(): BooleanArray {
        selectedRegions.clear()
        selectedRegions.addAll(regions.filter { r -> r.isAttached })
        return regions.map { it.isAttached }.toBooleanArray()
    }

    fun setNewRegions() {
        val request = AttachRegionsRequest(
            clientID,
            selectedRegions.map { it.ID }
        )
        sendRequest(
            ApeniApiService.getInstance().setRegions(request),
            successWithData = {
                getRegionForClient()
            }
        )
    }
}
