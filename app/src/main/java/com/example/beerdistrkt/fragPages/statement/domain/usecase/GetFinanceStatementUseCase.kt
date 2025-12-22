package com.example.beerdistrkt.fragPages.statement.domain.usecase

import com.example.beerdistrkt.fragPages.statement.domain.StatementRepository
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatement
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class GetFinanceStatementUseCase @Inject constructor(
    private val statementRepository: StatementRepository
) {
    suspend operator fun invoke(
        customerId: Int,
        offset: String?,
    ): ApiResponse<FinanceStatement> {
        return statementRepository.getFinancialStatement(customerId, offset)
    }
}