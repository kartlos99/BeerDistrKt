package com.example.beerdistrkt.fragPages.realisationtotal.models

import androidx.annotation.DrawableRes
import com.example.beerdistrkt.R
import com.squareup.moshi.Json

data class SaleRequestModel(
    val clientID: Int,
    val distributorID: Int,
    val comment: String?,
    val modifyUserID: Int,
    val isReplace: String = "0",

    val sales: List<SaleItem>? = null,
    val barrels: List<BarrelOutItem>? = null,
    val money: List<MoneyOutItem>? = null
) {

    data class SaleItem(
        val ID: Int = 0,
        val saleDate: String? = null,
        val beerID: Int,
//        val check: Boolean,
        val price: Double,
        val canTypeID: Int,
        val count: Int,
        val orderID: Int
    )

    data class BarrelOutItem(
        val ID: Int = 0,
        val outputDate: String? = null,
        val canTypeID: Int,
        val count: Int
    )

    data class MoneyOutItem(
        val ID: Int = 0,
        val takeMoneyDate: String? = null,
        val amount: Double,
        val paymentType: PaymentType
    )
}

enum class PaymentType(
    val value: String,
    @DrawableRes val iconRes: Int
) {
    @Json(name = "1")
    Cash("1", R.drawable.ic_cash),

    @Json(name = "2")
    Transfer("2", R.drawable.ic_transfer);
}