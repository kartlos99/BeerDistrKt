package com.example.beerdistrkt.fragPages.bottle.domain.usecase

import com.example.beerdistrkt.fragPages.bottle.domain.BottleRepository
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import javax.inject.Inject

class PutBottleUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke(
        bottle: Bottle
    ) = bottleRepository.putBottle(bottle)
}