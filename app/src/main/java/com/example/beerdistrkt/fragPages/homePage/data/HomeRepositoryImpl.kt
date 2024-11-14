package com.example.beerdistrkt.fragPages.homePage.data

import com.example.beerdistrkt.fragPages.beer.data.BeerMapper
import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.homePage.domain.HomeRepository
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class HomeRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val beerMapper: BeerMapper,
    private val beerRepository: BeerRepository,
    ioDispatcher: CoroutineDispatcher,
) : BaseRepository(ioDispatcher), HomeRepository {

    override suspend fun refreshBaseData() {
        apiCall {
            val data = api.getBaseData()
            beerRepository.setBeers(
                data.beers.map(beerMapper::toDomain)
            )
//            TODO set bottles & barrels
        }
    }

}