package com.example.beerdistrkt.fragPages.bottlemanagement.domain

import com.example.beerdistrkt.models.bottle.BaseBottleModel
import kotlinx.coroutines.flow.MutableStateFlow

interface BottleRepository {

    suspend fun getBottle(): List<BaseBottleModel>

    val bottleFlow: MutableStateFlow<List<BaseBottleModel>?>

    suspend fun setBottles(newData: List<BaseBottleModel>)
}