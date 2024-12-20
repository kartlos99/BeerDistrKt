package com.example.beerdistrkt.fragPages.beer.domain.model

import android.graphics.Color
import com.example.beerdistrkt.models.BeerStatus
import com.example.beerdistrkt.utils.DiffItem

data class Beer(
    val id: Int = 0,
    val name: String,
    val displayColor: Int = Color.rgb(128, 128, 128),
    val price: Double? = .0,
    val status: BeerStatus = BeerStatus.ACTIVE,
    val sortValue: Double = .0
) : DiffItem {

    override val key: Int
        get() = id

    val isActive: Boolean
        get() = status == BeerStatus.ACTIVE
}