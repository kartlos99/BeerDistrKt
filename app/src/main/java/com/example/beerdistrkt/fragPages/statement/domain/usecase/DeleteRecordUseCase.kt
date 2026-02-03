package com.example.beerdistrkt.fragPages.statement.domain.usecase

import com.example.beerdistrkt.fragPages.statement.domain.StatementRepository
import com.example.beerdistrkt.network.model.ResultState
import javax.inject.Inject

class DeleteRecordUseCase @Inject constructor(
    private val statementRepository: StatementRepository
) {
    suspend operator fun invoke(
        recordID: String,
        table: String,
    ): ResultState<Unit> = statementRepository.deleteRecord(recordID, table)
}