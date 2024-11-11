package com.example.beerdistrkt.fragPages.beer.domain

import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.network.api.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow

interface BeerRepository {

    suspend fun updateBeerSortValue(beerId: Int, sortValue: Double): ApiResponse<List<Beer>>

    suspend fun getBeers(): List<Beer>

    val beersFlow: MutableStateFlow<List<Beer>?>

    suspend fun refreshBeers()

    suspend fun putBeer(beer: Beer): ApiResponse<List<Beer>>

    suspend fun deleteBeer(beerId: Int): ApiResponse<List<Beer>>
}