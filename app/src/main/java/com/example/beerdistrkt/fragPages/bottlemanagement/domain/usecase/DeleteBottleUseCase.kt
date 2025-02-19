package com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase

import com.example.beerdistrkt.fragPages.bottlemanagement.domain.BottleRepository
import javax.inject.Inject

class DeleteBottleUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke(
        bottleId: Int
    ) = bottleRepository.deleteBottle(bottleId)
}