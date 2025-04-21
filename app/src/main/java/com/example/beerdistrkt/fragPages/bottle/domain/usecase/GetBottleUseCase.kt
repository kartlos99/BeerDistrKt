package com.example.beerdistrkt.fragPages.bottle.domain.usecase

import com.example.beerdistrkt.fragPages.bottle.domain.BottleRepository
import javax.inject.Inject

class GetBottleUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke(
        bottleId: Int
    ) = bottleRepository.getBottle(bottleId)
}