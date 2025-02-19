package com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase

import com.example.beerdistrkt.fragPages.bottlemanagement.domain.BottleRepository
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import javax.inject.Inject

class PutBottleUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke(
        bottle: BaseBottleModel
    ) = bottleRepository.putBottle(bottle)
}