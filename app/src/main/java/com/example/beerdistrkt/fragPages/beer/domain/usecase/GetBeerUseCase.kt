package com.example.beerdistrkt.fragPages.beer.domain.usecase

import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetBeerUseCase @Inject constructor(
    private val beerRepository: BeerRepository
) {
    suspend operator fun invoke(): List<Beer> {
        return beerRepository.getBeers()
    }

    fun beerAsFlow(): StateFlow<List<Beer>?> = beerRepository.beersFlow.asStateFlow()
}