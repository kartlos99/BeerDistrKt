package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultApiClient(
    private val api: DistributionApi,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), DistributionApiClient {

    companion object {
        private var instance: DistributionApiClient? = null

        fun getApi(): DistributionApiClient {
            return instance ?: DefaultApiClient(
                DistributionApi.getApi(),
                Dispatchers.IO
            ).also {
                instance = it
            }
        }
    }

    override suspend fun getStorehouseIoPaged(pageIndex: Int): ApiResponse<List<StorehouseIoDto>> {
        return apiCall { api.getStoreHouseIoPagedList(pageIndex) }
    }

}