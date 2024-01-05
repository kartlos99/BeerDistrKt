package com.example.beerdistrkt.network.model

import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.dto.BaseBottleModelDto

data class BaseDataResponse (
    val beers: List<BeerModelBase>,
    val bottles: List<BaseBottleModelDto>
)