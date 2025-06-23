package com.example.beerdistrkt.fragPages.homePage.data

import com.example.beerdistrkt.common.mapper.BarrelMapper
import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.fragPages.beer.data.BeerMapper
import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.bottle.domain.BottleRepository
import com.example.beerdistrkt.fragPages.homePage.domain.HomeRepository
import com.example.beerdistrkt.fragPages.bottle.data.BottleDtoMapper
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class HomeRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val beerMapper: BeerMapper,
    private val bottleMapper: BottleDtoMapper,
    private val beerRepository: BeerRepository,
    private val bottleRepository: BottleRepository,
    private val databaseDao: ApeniDatabaseDao,
    private val barrelMapper: BarrelMapper,
    private val commentMapper: CommentMapper,
    ioDispatcher: CoroutineDispatcher,
) : BaseRepository(ioDispatcher), HomeRepository {

    private var barrels: List<Barrel> = emptyList()

    init {
        databaseDao.getBarrels().observeForever {
            barrels = it.map(barrelMapper::toDomain)
        }
    }

    override suspend fun getBarrels(): List<Barrel> {
        return barrels
    }


    override suspend fun refreshBaseData() {
        apiCall {
            val data = api.getBaseData()
            beerRepository.setBeers(
                data.beers.map(beerMapper::toDomain)
            )
            bottleRepository.setBottles(
                data.bottles.map {
                    bottleMapper.map(it)
                }
            )
            databaseDao.insertBarrels(data.barrels)
        }
    }

    override suspend fun getComments(): ApiResponse<List<CommentModel>> {
        return apiCall {
            api.getComments().map {
                commentMapper.mapToDomain(it)
            }
        }
    }
}