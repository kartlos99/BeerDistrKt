package com.example.beerdistrkt.fragPages.sawyobi.domain.usecase

import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.StorehouseIoRepository
import javax.inject.Inject

class GetStoreHouseBalanceUseCase @Inject constructor(
    private val storehouseIoRepository: StorehouseIoRepository
) {
    suspend operator fun invoke(
        date: String,
        checkFlag: Int = 0,
    ): ApiResponse<StoreHouseResponse> {
        return storehouseIoRepository.getStoreBalance(date, checkFlag)
    }
}