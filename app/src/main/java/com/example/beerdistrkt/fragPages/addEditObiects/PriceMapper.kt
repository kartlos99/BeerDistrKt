package com.example.beerdistrkt.fragPages.addEditObiects

import com.example.beerdistrkt.fragPages.addEditObiects.model.PriceEditModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CustomerWithPrices
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.storage.ObjectCache

private const val NO_NAME = "name??"

class PriceMapper {

    private val beers = ObjectCache.getInstance()
        .getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID) ?: listOf()
    private val bottles = ObjectCache.getInstance()
        .getList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID) ?: listOf()

    fun getBeerPrices(data: CustomerWithPrices?): List<PriceEditModel> {
        return beers
            .filter { it.isActive }
            .map { beer ->
                val defaultPrice = beer.fasi ?: 0.0
                val price = data?.beerPrices
                    ?.firstOrNull {
                        it.beerID == beer.id
                    }?.fasi?.toDouble()
                    ?: defaultPrice

                PriceEditModel(
                    id = beer.id,
                    displayName = beer.dasaxeleba ?: NO_NAME,
                    defaultPrice = defaultPrice,
                    price = price
                )
            }
    }

    fun getBottlePrices(data: CustomerWithPrices?): List<PriceEditModel> {
        return bottles
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
