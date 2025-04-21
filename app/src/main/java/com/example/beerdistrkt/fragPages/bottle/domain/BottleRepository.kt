package com.example.beerdistrkt.fragPages.bottle.domain

import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.MutableStateFlow

interface BottleRepository {

    suspend fun getBottles(): List<Bottle>

    suspend fun getBottle(bottleId: Int): Bottle?

    val bottleFlow: MutableStateFlow<ResultState<List<Bottle>>>

    suspend fun refreshBottles()

    suspend fun putBottle(bottle: Bottle): ApiResponse<List<Bottle>>

    suspend fun deleteBottle(bottleId: Int): ApiResponse<List<Bottle>>

    suspend fun updateBottleSortValue(
        bottleId: Int,
        sortValue: Double,
    ): ApiResponse<List<Bottle>>

    suspend fun setBottles(bottles: List<Bottle>)
}