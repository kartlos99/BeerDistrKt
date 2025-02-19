package com.example.beerdistrkt.fragPages.bottlemanagement.domain

import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.network.api.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow

interface BottleRepository {

    suspend fun getBottles(): List<BaseBottleModel>

    suspend fun getBottle(bottleId: Int): BaseBottleModel?

    val bottleFlow: MutableStateFlow<List<BaseBottleModel>?>

    suspend fun refreshBottles()

    suspend fun putBottle(bottle: BaseBottleModel): ApiResponse<List<BaseBottleModel>>

    suspend fun deleteBottle(bottleId: Int): ApiResponse<List<BaseBottleModel>>

    suspend fun updateBottleSortValue(
        bottleId: Int,
        sortValue: Double,
    ): ApiResponse<List<BaseBottleModel>>

    suspend fun setBottles(bottles: List<BaseBottleModel>)
}