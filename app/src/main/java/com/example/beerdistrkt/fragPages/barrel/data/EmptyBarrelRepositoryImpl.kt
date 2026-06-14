package com.example.beerdistrkt.fragPages.barrel.data

import com.example.beerdistrkt.fragPages.barrel.data.mapper.EmptyBarrelsMapper
import com.example.beerdistrkt.fragPages.barrel.domain.EmptyBarrelRepository
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsInfo
import com.example.beerdistrkt.fragPages.beer.data.BeerMapper
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class EmptyBarrelRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val beerMapper: BeerMapper,
    private val emptyBarrelsMapper: EmptyBarrelsMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), EmptyBarrelRepository {

    override suspend fun getEmptyBarrelInfoByDate(date: String): ApiResponse<List<EmptyBarrelsInfo>> {

        return apiCall {
            api.getEmptyBarrelsInfoByDate(date).barrelInfo.mapNotNull {
                emptyBarrelsMapper.map(it)
            }
        }
    }

}