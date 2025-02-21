package com.example.beerdistrkt.fragPages.bottle.domain.model

import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.utils.DiffItem

data class Bottle(
    val id: Int,
    val name: String,
    val volume: Double,
    val actualVolume: Double,
    val beer: Beer,
    val price: Double,
    val status: BottleStatus,
    val sortValue: Double,
    val imageFileName: String?
) : DiffItem {

    override val key: Int
        get() = id

    val isActive: Boolean
        get() = status == BottleStatus.ACTIVE

    val isVisible: Boolean
        get() = status == BottleStatus.ACTIVE || status == BottleStatus.INACTIVE

    val imageLink: String?
        get() = imageFileName?.let { fileName ->
            BuildConfig.SERVER_URL + BOTTLE_IMAGES_FOLDER + fileName
        }

    companion object {
        const val BOTTLE_IMAGES_FOLDER = "images/"

        fun newInstance() = Bottle(
            id = 0,
            name = String.empty(),
            volume = .0,
            actualVolume = .0,
            beer = Beer.newInstance(),
            price = .0,
            status = BottleStatus.ACTIVE,
            sortValue = .0,
            imageFileName = String.empty()
        )
    }
}
