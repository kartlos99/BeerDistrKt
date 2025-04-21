package com.example.beerdistrkt.fragPages.bottle.data

import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.data.model.BottleDto
import javax.inject.Inject

interface BottleDtoMapper {
    suspend fun map(dto: BottleDto): Bottle
}

class DefaultBottleDtoMapper @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
) : BottleDtoMapper {

    override suspend fun map(dto: BottleDto): Bottle = with(dto) {
        val beers = getBeerUseCase()
        return Bottle(
            id = id,
            name = name,
            volume = volume,
            actualVolume = actualVolume,
            beer = beers.first {
                beerID == it.id
            },
            price = price,
            status = status,
            sortValue = sortValue,
            imageFileName = imageFileName
        )
    }
}