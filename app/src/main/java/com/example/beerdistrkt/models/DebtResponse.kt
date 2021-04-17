package com.example.beerdistrkt.models

import com.squareup.moshi.Json

data class DebtResponse(
    val clientID: Int,
    val clientName: String,
    val price: Double,
    val barrel: Int,
    val payed: Double,
    val barrelTakenBack: Int,
    val needCleaning: Int,
    val passDays: Int,
    val barrels: List<EmptyBarrel>
) {
    fun getMoneyDebt() = price - payed

    fun getBarrelDebt() = barrel - barrelTakenBack
}

data class EmptyBarrel(
    @Json(name = "dasaxeleba")
    val canTypeName: String,
    val canTypeID: Int,
    val balance: Int
)