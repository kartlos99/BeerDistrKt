package com.example.beerdistrkt.fragPages.beer.domain

import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.network.api.ApiResponse

interface BeerRepository {

    suspend fun updateBeerSortValue(beerId: Int, sortValue: Double): ApiResponse<List<Beer>>

    suspend fun getBeers(): List<Beer>

    suspend fun refreshBeers()
}