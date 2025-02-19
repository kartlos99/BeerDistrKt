package com.example.beerdistrkt.models.bottle

import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.models.bottle.dto.BaseBottleModelDto
import javax.inject.Inject

interface BottleDtoMapper {
    suspend fun map(dto: BaseBottleModelDto): BaseBottleModel
}

class DefaultBottleDtoMapper @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
) : BottleDtoMapper {

    override suspend fun map(dto: BaseBottleModelDto): BaseBottleModel = with(dto) {
        val beers = getBeerUseCase()
        return BaseBottleModel(
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