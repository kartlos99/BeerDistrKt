package com.example.beerdistrkt.fragPages.statement.domain

import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatement
import com.example.beerdistrkt.network.api.ApiResponse

interface StatementRepository {
    suspend fun getFinancialStatement(
        customerId: Int,
        offset: String?,
    ): ApiResponse<FinanceStatement>

    suspend fun getBarrelStatement(
        customerId: Int,
        offset: Int,
    ): ApiResponse<BarrelStatement>

}