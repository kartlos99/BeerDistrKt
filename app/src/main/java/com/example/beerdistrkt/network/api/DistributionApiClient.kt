package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto

interface DistributionApiClient {

    suspend fun getStorehouseIoPaged(pageIndex: Int): ApiResponse<List<StorehouseIoDto>>

}