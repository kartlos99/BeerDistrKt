package com.example.beerdistrkt.fragPages.customer.domain.mapper

import com.example.beerdistrkt.fragPages.customer.domain.model.PriceEditModel
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBeerPrice
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import javax.inject.Inject

class PriceMapper @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottlesUseCase: GetBottlesUseCase,
) {

    suspend fun getBeerPrices(beerPrices: List<ClientBeerPrice>?): List<PriceEditModel> {
        return getBeerUseCase()
            .filter { it.isActive }
            .map { beer ->
                val defaultPrice = beer.price ?: 0.0
                val price = beerPrices?.firstOrNull {
                    it.beerID == beer.id
                }?.price
                    ?: defaultPrice

                PriceEditModel(
                    id = beer.id,
                    displayName = beer.name,
                    defaultPrice = defaultPrice,
                    price = price
                )
            }
    }

    suspend fun getBottlePrices(bottlePrices: List<ClientBottlePrice>?): List<PriceEditModel> {
        return getBottlesUseCase()
            .filter { it.isActive }
            .map { bottle ->
                val defaultPrice = bottle.price
                val price = bottlePrices?.firstOrNull {
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
