package com.example.beerdistrkt.fragPages.customer.domain.mapper

import com.example.beerdistrkt.fragPages.customer.domain.model.PriceEditModel
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase.GetBottleUseCase
import com.example.beerdistrkt.models.CustomerWithPrices
import javax.inject.Inject

class PriceMapper @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottleUseCase: GetBottleUseCase,
) {

    suspend fun getBeerPrices(data: CustomerWithPrices?): List<PriceEditModel> {
        return getBeerUseCase()
            .filter { it.isActive }
            .map { beer ->
                val defaultPrice = beer.price ?: 0.0
                val price = data?.beerPrices
                    ?.firstOrNull {
                        it.beerID == beer.id
                    }?.fasi?.toDouble()
                    ?: defaultPrice

                PriceEditModel(
                    id = beer.id,
                    displayName = beer.name,
                    defaultPrice = defaultPrice,
                    price = price
                )
            }
    }

    suspend fun getBottlePrices(data: CustomerWithPrices?): List<PriceEditModel> {
        return getBottleUseCase()
            .filter { it.isActive }
            .map { bottle ->
                val defaultPrice = bottle.price
                val price = data?.bottlePrices
                    ?.firstOrNull {
                        it.bottleID == bottle.id
                    }?.price
                    ?: defaultPrice

                PriceEditModel(
                    id = bottle.id,
                    displayName = bottle.name,
                    defaultPrice = defaultPrice,
                    price = price
                )
            }
    }
}
