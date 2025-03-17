package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.squareup.moshi.Json

enum class BeerStatus(val value: String) {
    @Json(name = "0")
    DELETED("0"),

    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "2")
    INACTIVE("2"),
}

data class ObjToBeerPrice(
    @Json(name = "clientID")
    val clientID: Int,
    @Json(name = "beerID")
    val beerID: Int,
    @Json(name = "price")
    val price: Float
)

data class SaleInfo(
    val beerName: String,
    val price: Double,
    val litraji: Int
)

data class BarrelIO(
    val canTypeID: Int,
    val backCount: Int,
    val saleCount: Int
) {
    var barrelName: String? = null
}

data class MoneyInfo(
    val paymentType: PaymentType,
    val amount: Double
)