package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class StorehouseIoRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), StorehouseIoRepository {

    override suspend fun getStorehouseIoPaged(pageIndex: Int): ApiResponse<List<StorehouseIoDto>> {
        return apiCall { api.getStoreHouseIoPagedList(pageIndex) }
    }

    override suspend fun getStoreBalance(
        date: String,
        checkFlag: Int,
    ): ApiResponse<StoreHouseResponse> {
        return apiCall {
            api.getStoreHouseBalance(date, checkFlag)
        }
    }
}