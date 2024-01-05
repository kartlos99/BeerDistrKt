package com.example.beerdistrkt.fragPages.realisation.models

import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel

data class TempRealisationModel(
    var byBarrels: List<TempBeerItemModel>,
    var byBottles: List<TempBottleItemModel>
)
