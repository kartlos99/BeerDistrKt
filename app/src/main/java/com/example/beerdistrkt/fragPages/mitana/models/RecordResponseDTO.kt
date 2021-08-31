package com.example.beerdistrkt.fragPages.mitana.models

import com.example.beerdistrkt.fragPages.sales.models.PaymentType
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.TempBeerItemModel

data class RecordResponseDTO(
    val mitana: SaleRowModel? = null,
    val mout: MoneyRowModel? = null,
    val kout: BarrelRowModel? = null
)

data class SaleRowModel(
    val ID: Int,
    val saleDate: String,
    val clientID: Int,
    val distributorID: Int,
    val beerID: Int,
    val unitPrice: Double,
    val canTypeID: Int,
    val count: Int,
    val orderID: Int,
    val comment: String?
) {
    fun toTempBeerItemModel(barrels: List<CanModel>, beerList: List<BeerModel>): TempBeerItemModel {
        val canType = barrels.find { it.id == canTypeID }
        val beer = beerList.find { it.id == beerID }
        return TempBeerItemModel(
            ID = ID,
            beer = beer!!,
            canType = canType!!,
            count = count,
            onRemoveClick = {_: TempBeerItemModel -> }
        )
    }
}

data class MoneyRowModel(
    val ID: Int,
    val takeMoneyDate: String,
    val clientID: Int,
    val distributorID: Int,
    val amount: Double,
    val paymentType: PaymentType,
    val comment: String?
)

data class BarrelRowModel(
    val ID: Int,
    val outputDate: String,
    val clientID: Int,
    val distributorID: Int,
    val canTypeID: Int,
    val count: Int,
    val comment: String?
)
