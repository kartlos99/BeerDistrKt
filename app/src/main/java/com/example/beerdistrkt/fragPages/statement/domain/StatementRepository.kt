package com.example.beerdistrkt.fragPages.statement.domain

import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatement
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState

interface StatementRepository {
    suspend fun getFinancialStatement(
        customerId: Int,
        offset: String?,
    ): ApiResponse<FinanceStatement>

    suspend fun getBarrelStatement(
        customerId: Int,
        offset: String?,
    ): ApiResponse<BarrelStatement>

    suspend fun deleteRecord(
        recordID: String,
        table: String,
    ): ResultState<Unit>
}