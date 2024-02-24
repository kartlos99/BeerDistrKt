package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseIO
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseRepository
import com.example.beerdistrkt.fragPages.sawyobi.models.CombinedIoModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.flow.map

private const val ITEMS_PER_PAGE = 20

class StoreHouseListViewModel : BaseViewModel() {

    private val _ioDoneLiveData = MutableLiveData<ApiResponseState<List<CombinedIoModel>>>()
    val ioDoneLiveData: LiveData<ApiResponseState<List<CombinedIoModel>>>
        get() = _ioDoneLiveData

    private val beerList = ObjectCache.getInstance()
        .getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID) ?: mutableListOf()
    private val bottleList = ObjectCache.getInstance()
        .getList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID) ?: mutableListOf()

    val items = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { StorehouseRepository().getPagingSource() }
    )
        .flow
        .map { pagingData ->
            pagingData.map { ioDto ->
                StorehouseIO.fromDto(ioDto)
            }

        }
        .cachedIn(viewModelScope)

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
