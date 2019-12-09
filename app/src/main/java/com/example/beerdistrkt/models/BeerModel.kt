package com.example.beerdistrkt.models

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

class SaleInfo(
    val beerName: String,
    val pr: Float,
    val litraji: Int,
    val k30: Float,
    val k50: Float
) : Serializable

