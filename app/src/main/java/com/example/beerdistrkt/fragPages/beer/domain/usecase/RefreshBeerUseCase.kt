package com.example.beerdistrkt.fragPages.beer.domain.usecase

import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import javax.inject.Inject

class RefreshBeerUseCase @Inject constructor(
    private val beerRepository: BeerRepository
) {
    suspend operator fun invoke() = beerRepository.refreshBeers()
}