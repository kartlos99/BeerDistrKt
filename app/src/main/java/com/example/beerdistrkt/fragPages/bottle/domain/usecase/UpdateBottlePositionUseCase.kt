package com.example.beerdistrkt.fragPages.bottle.domain.usecase

import com.example.beerdistrkt.fragPages.bottle.domain.BottleRepository
import javax.inject.Inject

class UpdateBottlePositionUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke(
        bottleId: Int,
        sortValue: Double,
    ) = bottleRepository.updateBottleSortValue(bottleId, sortValue)
}