package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase.GetBottleUseCase
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseIO
import com.example.beerdistrkt.fragPages.sawyobi.domain.GetStorehouseIoPagingSourceUseCase
import com.example.beerdistrkt.fragPages.sawyobi.models.StorehouseIoPm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20

@HiltViewModel
class StoreHouseListViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottleUseCase: GetBottleUseCase,
    private val getStorehouseIoPagingSourceUseCase: GetStorehouseIoPagingSourceUseCase,
) : BaseViewModel() {

    private var beerList: List<Beer> = listOf()

    val eventSharedFlow: MutableSharedFlow<String> = MutableSharedFlow()

    val items = Pager(
        config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
        pagingSourceFactory = { getStorehouseIoPagingSourceUseCase() }
    )
        .flow
        .map { pagingData ->
            pagingData.map { ioDto ->

                StorehouseIoPm.fromDomainIo(
                    StorehouseIO.fromDto(ioDto, beerList, getBottleUseCase()),
                    ::onItemClick
                )

            }

        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            beerList = getBeerUseCase()
        }
    }

    fun onItemClick(groupID: String) {
        viewModelScope.launch {
            eventSharedFlow.emit(groupID)
        }
    }
}
