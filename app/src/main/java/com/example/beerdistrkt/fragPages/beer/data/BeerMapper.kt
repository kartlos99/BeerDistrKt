package com.example.beerdistrkt.fragPages.beer.data

import android.graphics.Color
import com.example.beerdistrkt.asHexColor
import com.example.beerdistrkt.fragPages.beer.data.model.BeerDto
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import javax.inject.Inject

class BeerMapper @Inject constructor() {

    fun toDomain(beerDto: BeerDto): Beer {
        return Beer(
            beerDto.id,
            beerDto.name,
            Color.parseColor(beerDto.color),
            beerDto.price,
            beerDto.status,
            beerDto.sortValue,
        )
    }

    fun toDto(beer: Beer) = BeerDto(
        beer.id,
        beer.name,
        beer.displayColor.asHexColor(),
        beer.price,
        beer.status,
        beer.sortValue,
    )
}