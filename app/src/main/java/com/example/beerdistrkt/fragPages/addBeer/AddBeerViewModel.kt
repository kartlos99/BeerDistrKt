package com.example.beerdistrkt.fragPages.addBeer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState

class AddBeerViewModel : BaseViewModel() {

    val beerList = ObjectCache.getInstance().getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID)
        ?: mutableListOf()

    private val _addBeerLiveData = MutableLiveData<ApiResponseState<String>>()
    val addBeerLiveData: LiveData<ApiResponseState<String>>
        get() = _addBeerLiveData

    private val _deleteBeerLiveData = MutableLiveData<ApiResponseState<String>>()
    val deleteBeerLiveData: LiveData<ApiResponseState<String>>
        get() = _deleteBeerLiveData

    fun sendDataToDB(beer: BeerModelBase) {

        _addBeerLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addBeer(beer),
            successWithData = {
                _addBeerLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = { code, error ->
                _addBeerLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                _addBeerLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun removeBeer(beerId: Int) {

        _deleteBeerLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().deleteBeer(DeleteBeerModel(beerId)),
            successWithData = {
                _deleteBeerLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = { code, error ->
                _deleteBeerLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                _deleteBeerLiveData.value = ApiResponseState.Loading(false)
            }
        )

    }
}
