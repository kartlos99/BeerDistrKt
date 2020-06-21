package com.example.beerdistrkt.fragPages.sales.models

data class SaleRequestModel(
    val clientID: Int,
    val distributorID: Int,
    val comment: String?,
    val modifyUserID: Int,

    val sales: List<SaleItem>? = null,
    val barrels: List<BarrelOutItem>? = null,
    val money: MoneyOutItem? = null
) {

    data class SaleItem(
        val ID: Int =0,
        val saleDate: String? = null,
        val beerID: Int,
//        val check: Boolean,
        val price: Double,
        val canTypeID: Int,
        val count: Int,
        val orderID: Int
    )

    data class BarrelOutItem(
        val ID: Int =0,
        val outputDate: String? = null,
        val canTypeID: Int,
        val count: Int
    )

    data class MoneyOutItem(
        val ID: Int =0,
        val takeMoneyDate: String? = null,
        val amount: Double
    )
}