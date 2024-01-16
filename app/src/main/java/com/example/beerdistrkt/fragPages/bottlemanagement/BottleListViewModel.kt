package com.example.beerdistrkt.fragPages.bottlemanagement

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.storage.ObjectCache

class BottleListViewModel : BaseViewModel() {

    val bottleList: List<BaseBottleModel>
        get() = ObjectCache.getInstance().getList(
            BaseBottleModel::class,
            ObjectCache.BOTTLE_LIST_ID
        )
            ?: mutableListOf()

}