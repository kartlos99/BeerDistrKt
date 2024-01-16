package com.example.beerdistrkt.fragPages.bottlemanagement

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.storage.ObjectCache
import kotlinx.coroutines.flow.MutableStateFlow

class BottleListViewModel: BaseViewModel() {


    private val bottleList = ObjectCache.getInstance().getList(
        BaseBottleModel::class,
        ObjectCache.BOTTLE_LIST_ID
    )
        ?: mutableListOf()

    val bottleListFlow: MutableStateFlow<List<BaseBottleModel>> = MutableStateFlow(bottleList)

}