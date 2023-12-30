package com.example.beerdistrkt.models.bottle

data class ClientBottlePrice(
    val id: Int,
    val clientID: Int,
    val bottleID: Int,
    val price: Double
)
