package com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase

import com.example.beerdistrkt.fragPages.bottlemanagement.domain.BottleRepository
import javax.inject.Inject

class RefreshBottlesUseCase @Inject constructor(
    private val bottleRepository: BottleRepository,
) {
    suspend operator fun invoke() = bottleRepository.refreshBottles()
}