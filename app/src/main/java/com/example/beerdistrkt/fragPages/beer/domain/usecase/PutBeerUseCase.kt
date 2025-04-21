package com.example.beerdistrkt.fragPages.beer.domain.usecase

import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class PutBeerUseCase @Inject constructor(
    private val repository: BeerRepository
) {
    suspend operator fun invoke(beer: Beer): ApiResponse<List<Beer>> {
        return repository.putBeer(beer)
    }
}