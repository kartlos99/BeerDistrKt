package com.example.beerdistrkt.models

import androidx.room.Entity
import com.squareup.moshi.Json
import java.io.Serializable
import java.util.*

data class BeerModel (
    var id: Int = 0,
    var dasaxeleba: String? = null,
    var displayColor: String? = null,
    var fasi: Double? = null
)

data class PeerObjPrice(var obj_id: Int) {
    var fasebi = ArrayList<Float>()
}

@Entity(tableName = "prices_table", primaryKeys = ["objID", "beerID"])
data class ObjToBeerPrice(
    @Json(name = "obj_id")
    val objID:  Int,
    @Json(name = "beer_id")
    val beerID: Int,
    val fasi: Float
)

class SaleInfo(
    val beerName: String,
    val pr: Float,
    val litraji: Int,
    val k30: Float,
    val k50: Float
) : Serializable

