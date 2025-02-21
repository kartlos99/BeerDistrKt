package com.example.beerdistrkt.fragPages.bottle.data

import com.example.beerdistrkt.fragPages.bottle.data.model.BottleOrderUpdateDto
import com.example.beerdistrkt.fragPages.bottle.domain.BottleRepository
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
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

    private var bottles: List<Bottle> = emptyList()

    override val bottleFlow: MutableStateFlow<ResultState<List<Bottle>>> =
        MutableStateFlow(ResultState.Loading)

    override suspend fun getBottles(): List<Bottle> {
        if (bottles.isEmpty())
            fetchBottles()
        return bottles
    }

    override suspend fun getBottle(bottleId: Int): Bottle? {
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

    override suspend fun putBottle(bottle: Bottle): ApiResponse<List<Bottle>> {
        return apiCall {
            api.putBottle(bottleMapper.toDto(bottle))
                .map { bottleDtoMapper.map(it) }
                .also {
                    bottles = it
                }
        }.also {
            bottleFlow.emit(it.toResultState())
        }
    }

    override suspend fun deleteBottle(bottleId: Int): ApiResponse<List<Bottle>> {
        return apiCall {
            api.deleteBottle(bottleId)
                .map { bottleDtoMapper.map(it) }
                .also {
                    bottles = it
                }
        }.also {
            bottleFlow.emit(it.toResultState())
        }
    }

    override suspend fun updateBottleSortValue(
        bottleId: Int,
        sortValue: Double
    ): ApiResponse<List<Bottle>> {
        return apiCall {
            api.updateBottleSortValue(BottleOrderUpdateDto(bottleId, sortValue))
                .map { bottleDtoMapper.map(it) }
                .also {
                    bottles = it
                }
        }.also {
            bottleFlow.emit(it.toResultState())
        }
    }

    private suspend fun fetchBottles() {
        bottleFlow.emit(ResultState.Loading)
        apiCall {
            api.getBottles()
                .map {
                    bottleDtoMapper.map(it)
                }
                .also {
                    bottles = it
                }
        }.also {
            bottleFlow.emit(it.toResultState())
        }
    }

    override suspend fun setBottles(bottles: List<Bottle>) {
        this.bottles = bottles
        bottleFlow.emit(bottles.asSuccessState())
    }
}