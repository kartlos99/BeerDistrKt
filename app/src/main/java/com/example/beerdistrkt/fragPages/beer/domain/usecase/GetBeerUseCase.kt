package com.example.beerdistrkt.fragPages.beer.domain.usecase

import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import javax.inject.Inject

class GetBeerUseCase @Inject constructor(
    private val beerRepository: BeerRepository
) {
    suspend operator fun invoke(): List<Beer> {
        return beerRepository.getBeers()
    }
}