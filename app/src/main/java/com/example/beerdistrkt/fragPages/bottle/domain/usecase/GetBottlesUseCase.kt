package com.example.beerdistrkt.fragPages.bottle.domain.usecase

import com.example.beerdistrkt.fragPages.bottle.domain.BottleRepository
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetBottlesUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke() = bottleRepository.getBottles()

    fun asFlow(): StateFlow<ResultState<List<Bottle>>> = bottleRepository.bottleFlow.asStateFlow()
}