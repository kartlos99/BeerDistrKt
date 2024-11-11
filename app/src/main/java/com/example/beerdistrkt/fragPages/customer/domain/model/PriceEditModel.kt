package com.example.beerdistrkt.fragPages.customer.domain.model

data class PriceEditModel(
    val id: Int,
    val displayName: String,
    val defaultPrice: Double,
    val price: Double
)
