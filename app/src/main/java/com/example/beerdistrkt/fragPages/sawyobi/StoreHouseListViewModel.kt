package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sawyobi.models.CombinedIoModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState

class StoreHouseListViewModel : BaseViewModel() {

    private val _ioDoneLiveData = MutableLiveData<ApiResponseState<List<CombinedIoModel>>>()
    val ioDoneLiveData: LiveData<ApiResponseState<List<CombinedIoModel>>>
        get() = _ioDoneLiveData

    private val beerList =
        ObjectCache.getInstance().getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID)
            ?: mutableListOf()
    private val bottleList = ObjectCache.getInstance().getList(
        BaseBottleModel::class,
        ObjectCache.BOTTLE_LIST_ID
    )
        ?: mutableListOf()

    init {
        getIoList()
    }

    private fun getIoList() {
        _ioDoneLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseIoList(""),
            successWithData = {
                _ioDoneLiveData.value = ApiResponseState.Success(
                    it.merge(beerList, bottleList)
                )
            },
            finally = {
                _ioDoneLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }
}
