package com.example.beerdistrkt.fragPages.beer.data

import com.example.beerdistrkt.fragPages.beer.data.model.BeerOrderingUpdateDto
import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class BeerRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val beerMapper: BeerMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), BeerRepository {

    private var beers: List<Beer> = emptyList()

    override val beersFlow: MutableStateFlow<List<Beer>?> = MutableStateFlow(null)

    override suspend fun updateBeerSortValue(
        beerId: Int,
        sortValue: Double
    ): ApiResponse<List<Beer>> {
        return apiCall {
            api.updateBeerSortValue(
                BeerOrderingUpdateDto(beerId, sortValue)
            )
                .map(beerMapper::toDomain)
                .also {
                    beers = it
                    beersFlow.emit(it)
                }
        }
    }

    override suspend fun getBeers(): List<Beer> {
        if (beers.isEmpty())
            fetchBeers()
        return beers
    }

    private suspend fun fetchBeers() {
        apiCall {
            api.getBeers()
                .map(beerMapper::toDomain)
                .also {
                    beers = it
                    beersFlow.emit(it)
                }
        }
    }

    override suspend fun refreshBeers() {
        fetchBeers()
    }

    override suspend fun putBeer(beer: Beer): ApiResponse<List<Beer>> {
        return apiCall {
            api.putBeer(beerMapper.toDto(beer))
                .map(beerMapper::toDomain)
                .also {
                    beers = it
                    beersFlow.emit(it)
                }
        }
    }

    override suspend fun deleteBeer(beerId: Int): ApiResponse<List<Beer>> {
        return apiCall {
            api.deleteBeer(beerId)
                .map(beerMapper::toDomain)
                .also {
                    beers = it
                    beersFlow.emit(it)
                }
        }
    }

    override suspend fun setBeers(beers: List<Beer>) {
        this.beers = beers
        beersFlow.emit(beers)
    }
}