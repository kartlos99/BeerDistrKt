package com.example.beerdistrkt.fragPages.bottlemanagement.data

import com.example.beerdistrkt.fragPages.bottlemanagement.data.model.BottleOrderUpdateDto
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.BottleRepository
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.BottleDtoMapper
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class BottleRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val bottleDtoMapper: BottleDtoMapper,
    private val bottleMapper: BottleMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), BottleRepository {

    private var bottles: List<BaseBottleModel> = emptyList()

    override val bottleFlow: MutableStateFlow<List<BaseBottleModel>?> = MutableStateFlow(null)

    override suspend fun getBottles(): List<BaseBottleModel> {
        if (bottles.isEmpty())
            fetchBottles()
        return bottles
    }

    override suspend fun getBottle(bottleId: Int): BaseBottleModel? {
        val bottle = bottles.find { it.id == bottleId }
        return if (bottle == null) {
            fetchBottles()
            bottles.find { it.id == bottleId }
        } else {
            bottle
        }
    }

    override suspend fun refreshBottles() {
        fetchBottles()
    }

    override suspend fun putBottle(bottle: BaseBottleModel): ApiResponse<List<BaseBottleModel>> {
        return apiCall {
            api.putBottle(bottleMapper.toDto(bottle))
                .map { bottleDtoMapper.map(it) }
                .also {
                    bottles = it
                    bottleFlow.emit(it)
                }
        }
    }

    override suspend fun deleteBottle(bottleId: Int): ApiResponse<List<BaseBottleModel>> {
        return apiCall {
            api.deleteBottle(bottleId)
                .map { bottleDtoMapper.map(it) }
                .also {
                    bottles = it
                    bottleFlow.emit(it)
                }
        }
    }

    override suspend fun updateBottleSortValue(
        bottleId: Int,
        sortValue: Double
    ): ApiResponse<List<BaseBottleModel>> {
        return apiCall {
            api.updateBottleSortValue(BottleOrderUpdateDto(bottleId, sortValue))
                .map { bottleDtoMapper.map(it) }
                .also {
                    bottles = it
                    bottleFlow.emit(it)
                }
        }
    }

    private suspend fun fetchBottles() {
        apiCall {
            api.getBottles()
                .map {
                    bottleDtoMapper.map(it)
                }
                .also {
                    bottles = it
                    bottleFlow.emit(it)
                }
        }
    }

    override suspend fun setBottles(bottles: List<BaseBottleModel>) {
        this.bottles = bottles
        bottleFlow.emit(bottles)
    }
}