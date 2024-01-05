package com.example.beerdistrkt.models.bottle

import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.dto.BaseBottleModelDto

interface BottleDtoMapper {
    fun map(dto: BaseBottleModelDto): BaseBottleModel
}

class DefaultBottleDtoMapper(
    private val beers: List<BeerModelBase>
) : BottleDtoMapper {

    override fun map(dto: BaseBottleModelDto): BaseBottleModel = with(dto) {

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
            imageLink = BuildConfig.SERVER_URL + "images/" + imageFileName
        )
    }
}