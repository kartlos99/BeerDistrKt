package com.example.beerdistrkt.fragPages.bottle.data

import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.data.model.BottleDto
import javax.inject.Inject

class BottleMapper @Inject constructor() {

    fun toDto(bottle: Bottle) = BottleDto(
        id = bottle.id,
        name = bottle.name,
        volume = bottle.volume,
        actualVolume = bottle.actualVolume,
        beerID = bottle.beer.id,
        price = bottle.price,
        status = bottle.status,
        sortValue = bottle.sortValue,
        imageFileName = bottle.imageFileName,
    )
}