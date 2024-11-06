package com.example.beerdistrkt.fragPages.beer.domain.model

import android.graphics.Color
import com.example.beerdistrkt.models.BeerStatus
import com.example.beerdistrkt.utils.DiffItem

data class Beer(
    var id: Int = 0,
    var name: String,
    var displayColor: Int = Color.rgb(128, 128, 128),
    var price: Double? = .0,
    var status: BeerStatus = BeerStatus.ACTIVE,
    var sortValue: Double
) : DiffItem {

    override val key: Int
        get() = id

    val isActive: Boolean
        get() = status == BeerStatus.ACTIVE
}