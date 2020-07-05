package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.storage.ObjectCache

class SawyobiViewModel : ViewModel() {

    val beerList = ObjectCache.getInstance().getList(BeerModel::class, "beerList")
        ?: mutableListOf()
    // TODO: Implement the ViewModel
}
