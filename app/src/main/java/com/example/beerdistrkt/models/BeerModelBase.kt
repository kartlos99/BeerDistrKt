package com.example.beerdistrkt.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.utils.DiffItem
import com.squareup.moshi.Json

@Entity(tableName = "beer_table")
data class BeerModelBase(
    @PrimaryKey
    var id: Int = 0,
    var dasaxeleba: String? = null,
    @Json(name = "color")
    var displayColor: String? = null,
    var fasi: Double? = null,
    @Json(name = "active")
    var status: BeerStatus = BeerStatus.ACTIVE,
    var sortValue: String = ""
): DiffItem {

    override val key: Int
        get() = id

    val isActive: Boolean
        get() = status == BeerStatus.ACTIVE
}

enum class BeerStatus(val value: String) {
    @Json(name = "0")
    DELETED("0"),

    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "2")
    INACTIVE("2"),
}

data class PeerObjPrice(var obj_id: Int) {
    var fasebi = ArrayList<Float>()
}

@Entity(tableName = "prices_table", primaryKeys = ["objID", "beerID"])
data class ObjToBeerPrice(
    @Json(name = "obj_id")
    val objID: Int,
    @Json(name = "beer_id")
    val beerID: Int,
    val fasi: Float
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