package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState

class StoreHouseListViewModel : BaseViewModel() {

    private val beerLiveData = database.getBeerList()
    lateinit var beerMap : Map<Int, BeerModel>

    private val _ioDoneLiveData = MutableLiveData<ApiResponseState<List<IoModel>>>()
    val ioDoneLiveData: LiveData<ApiResponseState<List<IoModel>>>
        get() = _ioDoneLiveData

    init {
        beerLiveData.observeForever { beerList ->
            beerMap = beerList.groupBy { it.id }.mapValues {
                it.value[0]
            }
        }
        getIoList()
    }

    private fun getIoList() {
        _ioDoneLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseIoList(""),
            successWithData = {
                _ioDoneLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                _ioDoneLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }
}
