package com.example.beerdistrkt.fragPages.barrel.domain

import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsInfo
import com.example.beerdistrkt.network.api.ApiResponse

interface EmptyBarrelRepository {

    suspend fun getEmptyBarrelInfoByDate(date: String): ApiResponse<List<EmptyBarrelsInfo>>
}