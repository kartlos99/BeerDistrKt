package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse

interface StorehouseIoRepository {

    suspend fun getStorehouseIoPaged(pageIndex: Int): ApiResponse<List<StorehouseIoDto>>
    suspend fun getStoreBalance(
        date: String,
        checkFlag: Int,
    ): ApiResponse<StoreHouseResponse>
}