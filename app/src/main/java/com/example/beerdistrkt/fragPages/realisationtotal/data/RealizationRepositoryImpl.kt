package com.example.beerdistrkt.fragPages.realisationtotal.data

import com.example.beerdistrkt.fragPages.realisationtotal.domain.RealizationRepository
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class RealizationRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val realizationDayMapper: RealizationDayMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), RealizationRepository {


    override suspend fun getRealizationDay(
        date: String,
        distributorId: Int
    ): ApiResponse<RealizationDay> {

        return apiCall {
            realizationDayMapper.map(api.getRealizationDayInfo(date, distributorId))
        }
    }
}
