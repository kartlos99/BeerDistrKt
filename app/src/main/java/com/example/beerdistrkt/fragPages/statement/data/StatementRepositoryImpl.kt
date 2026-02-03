package com.example.beerdistrkt.fragPages.statement.data

import com.example.beerdistrkt.fragPages.statement.data.mapper.BarrelStatementMapper
import com.example.beerdistrkt.fragPages.statement.data.mapper.FinanceStatementMapper
import com.example.beerdistrkt.fragPages.statement.domain.StatementRepository
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatement
import com.example.beerdistrkt.fragPages.user.data.model.DeleteRecordApiModel
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class StatementRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val financeStatementMapper: FinanceStatementMapper,
    private val barrelStatementMapper: BarrelStatementMapper,
    ioDispatcher: CoroutineDispatcher,
) : StatementRepository, BaseRepository(ioDispatcher) {


    override suspend fun getFinancialStatement(
        customerId: Int,
        offset: String?,
    ): ApiResponse<FinanceStatement> {
        return apiCall {
            val data = api.getFinancialStatement(customerId, offset)
            financeStatementMapper.mapToDomain(data)
        }
    }

    override suspend fun getBarrelStatement(
        customerId: Int,
        offset: Int,
    ): ApiResponse<BarrelStatement> {
        return apiCall {
            val data = api.getBarrelStatement(customerId, offset)
            barrelStatementMapper.mapToDomain(data)
        }
    }

    override suspend fun deleteRecord(
        recordID: String,
        table: String,
    ): ResultState<Unit> {
        return apiCall {
            api.deleteRecord(
                DeleteRecordApiModel(
                    recordID = recordID,
                    table = table
                )
            )
        }.toResultState()
    }
}