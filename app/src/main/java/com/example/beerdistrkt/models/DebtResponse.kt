package com.example.beerdistrkt.models

data class DebtResponse(
    val clientID: Int,
    val clientName: String,
    val price: Double,
    val barrel: Int,
    val payed: Double,
    val barrelTakenBack: Int,
    val needCleaning: Int
) {
    fun getMoneyDebt() = price - payed

    fun getBarrelDebt() = barrel - barrelTakenBack
}