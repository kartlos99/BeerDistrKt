package com.example.beerdistrkt.fragPages.realisationtotal.domain.usecase

import com.example.beerdistrkt.fragPages.realisationtotal.domain.RealizationRepository
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import javax.inject.Inject

class GetRealizationDayUseCase @Inject constructor(
    private val realizationRepository: RealizationRepository
) {
    suspend operator fun invoke(
        date: String,
        distributorId: Int
    ): ResultState<RealizationDay> {
        return realizationRepository.getRealizationDay(date, distributorId).toResultState()
    }
}