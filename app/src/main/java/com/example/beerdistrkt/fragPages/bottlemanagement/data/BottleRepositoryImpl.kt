package com.example.beerdistrkt.fragPages.bottlemanagement.data

import com.example.beerdistrkt.fragPages.bottlemanagement.domain.BottleRepository
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.BottleDtoMapper
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class BottleRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val bottleMapper: BottleDtoMapper,
    ioDispatcher: CoroutineDispatcher
): BaseRepository(ioDispatcher), BottleRepository {

    private var bottles: List<BaseBottleModel> = emptyList()

//    TODO move bottle related actions here

    override suspend fun getBottle(): List<BaseBottleModel> {
        return bottles
    }

    override val bottleFlow: MutableStateFlow<List<BaseBottleModel>?> = MutableStateFlow(null)

    override suspend fun setBottles(newData: List<BaseBottleModel>) {
        bottles = newData
        bottleFlow.emit(newData)
    }
}