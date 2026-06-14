package com.example.beerdistrkt.fragPages.barrel.domain.usecase

import com.example.beerdistrkt.fragPages.barrel.domain.EmptyBarrelRepository
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsInfo
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class GetEmptyBarrelsInfoUseCase @Inject constructor(
    private val emptyBarrelRepository: EmptyBarrelRepository,
) {
    suspend operator fun invoke(date: String): ApiResponse<List<EmptyBarrelsInfo>> {
        return emptyBarrelRepository.getEmptyBarrelInfoByDate(date)
    }
}