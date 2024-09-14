package com.example.beerdistrkt.fragPages.realisationtotal.domain

import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import com.example.beerdistrkt.network.api.ApiResponse

interface RealizationRepository {

    suspend fun getRealizationDay(
        date: String,
        distributorId: Int
    ): ApiResponse<RealizationDay>
}