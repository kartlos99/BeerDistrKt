package com.example.beerdistrkt.fragPages.realisation.models

import com.example.beerdistrkt.areNotNull
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel
import com.squareup.moshi.Json

data class RecordResponseDTO(
    val mitana: SaleRowModel? = null,
    @Json(name = "mitana_bottle")
    val mitanaBottle: SaleBottleRowModel? = null,
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
    fun toTempBeerItemModel(
        barrels: List<CanModel>,
        beerList: List<Beer>
    ): TempBeerItemModel? {
        val canType = barrels.find { it.id == canTypeID }
        val beer = beerList.find { it.id == beerID }

        return takeUnless {
            !areNotNull(canType, beer)
        }?.let {
            TempBeerItemModel(
                ID = ID,
                beer = beer!!,
                canType = canType!!,
                count = count,
                onRemoveClick = { _: TempBeerItemModel -> },
                orderItemID = ID // TODO rename this field or made another model
            )
        }
    }
}

data class SaleBottleRowModel(
    val ID: Int,
    val saleDate: String,
    val clientID: Int,
    val distributorID: Int,
    val bottleID: Int,
    val price: Double,
    val count: Int,
    val orderID: Int,
    val comment: String?
) {
    fun toTempBottleItemModel(bottles: List<BaseBottleModel>): TempBottleItemModel? {
        bottles
            .firstOrNull { it.id == bottleID }
            ?.let { bottle ->
                return TempBottleItemModel(
                    id = ID,
                    bottle = bottle,
                    count = count,
                    onRemoveClick = { _: TempBottleItemModel -> },
                    orderItemID = ID // TODO rename this field or made another model
                )
            } ?: return null

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
