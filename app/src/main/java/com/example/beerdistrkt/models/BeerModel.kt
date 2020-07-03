package com.example.beerdistrkt.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

@Entity(tableName = "beer_table")
data class BeerModel(
    @PrimaryKey
    var id: Int = 0,
    var dasaxeleba: String? = null,
    @Json(name = "color")
    var displayColor: String? = null,
    var fasi: Double? = null,
    val sortValue: String = ""
)

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

data class RealizationDay(
    val sale: List<SaleInfo>,
    val takenMoney: Double,
    val barrels: List<BarrelIO>,
    val xarji: List<Xarji>
)