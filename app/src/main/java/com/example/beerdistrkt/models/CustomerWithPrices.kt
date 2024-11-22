package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice

data class CustomerWithPrices(
    val customer: Customer,
    var beerPrices: List<ObjToBeerPrice>,
    var bottlePrices: List<ClientBottlePrice>,
)
