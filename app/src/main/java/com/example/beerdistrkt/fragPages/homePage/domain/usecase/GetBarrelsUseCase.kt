package com.example.beerdistrkt.fragPages.homePage.domain.usecase

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.homePage.domain.HomeRepository
import javax.inject.Inject

class GetBarrelsUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): List<Barrel> {
        return homeRepository.getBarrels()
    }
}