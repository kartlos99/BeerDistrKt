package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseIO
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseRepository
import com.example.beerdistrkt.fragPages.sawyobi.models.StorehouseIoPm
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.storage.ObjectCache
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val ITEMS_PER_PAGE = 20

class StoreHouseListViewModel : BaseViewModel() {

    private val beerList = ObjectCache.getInstance()
        .getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID) ?: mutableListOf()
    private val bottleList = ObjectCache.getInstance()
        .getList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID) ?: mutableListOf()

    val eventSharedFlow: MutableSharedFlow<String> = MutableSharedFlow()

    val items = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { StorehouseRepository().getPagingSource() }
    )
        .flow
        .map { pagingData ->
            pagingData.map { ioDto ->

                StorehouseIoPm.fromDomainIo(
                    StorehouseIO.fromDto(ioDto, beerList, bottleList),
                    ::onItemClick
                )

            }

        }
        .cachedIn(viewModelScope)

    fun onItemClick(groupID: String) {
        viewModelScope.launch {
            eventSharedFlow.emit(groupID)
        }
    }
}
