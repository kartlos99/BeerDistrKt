package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.objList.model.Customer
import com.example.beerdistrkt.models.bottle.ClientBottlePrice

data class CustomerWithPrices(
    val customer: Customer,
    var beerPrices: List<ObjToBeerPrice>,
    var bottlePrices: List<ClientBottlePrice>,
)
