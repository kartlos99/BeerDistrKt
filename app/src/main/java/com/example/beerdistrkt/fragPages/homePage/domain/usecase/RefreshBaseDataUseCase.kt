package com.example.beerdistrkt.fragPages.homePage.domain.usecase

import com.example.beerdistrkt.fragPages.homePage.domain.HomeRepository
import javax.inject.Inject

class RefreshBaseDataUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke() = homeRepository.refreshBaseData()
}