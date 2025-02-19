package com.example.beerdistrkt.models.bottle

import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer

data class BaseBottleModel(
    val id: Int,
    val name: String,
    val volume: Double,
    val actualVolume: Double,
    val beer: Beer,
    val price: Double,
    val status: BottleStatus,
    val sortValue: String,
    val imageFileName: String?
) {
    val isActive: Boolean
        get() = status == BottleStatus.ACTIVE

    val imageLink: String?
        get() = imageFileName?.let { fileName ->
            BuildConfig.SERVER_URL + BOTTLE_IMAGES_FOLDER + fileName
        }

    companion object {
        const val BOTTLE_IMAGES_FOLDER = "images/"
    }
}
