package com.example.beerdistrkt.fragPages.bottlemanagement.data

import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.dto.BaseBottleModelDto
import javax.inject.Inject

class BottleMapper @Inject constructor() {

    fun toDto(bottle: BaseBottleModel) = BaseBottleModelDto(
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