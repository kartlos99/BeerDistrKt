package com.example.beerdistrkt.fragPages.beer.data

import com.example.beerdistrkt.fragPages.beer.data.model.BeerDto
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import javax.inject.Inject

class BeerMapper @Inject constructor() {

    fun toDomain(beerDto: BeerDto): Beer {
        return Beer(
            beerDto.id,
            beerDto.name,
            beerDto.color,
            beerDto.price,
            beerDto.status,
            beerDto.sortValue,
        )
    }
}