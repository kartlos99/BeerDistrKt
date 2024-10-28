package com.example.beerdistrkt.fragPages.beer.domain.usecase

import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import javax.inject.Inject

class UpdateBeerPositionUseCase @Inject constructor(
    private val beerRepository: BeerRepository
) {
    suspend operator fun invoke(beerId: Int, sortValue: Double) {
        beerRepository.updateBeerSortValue(beerId, sortValue)
    }
}