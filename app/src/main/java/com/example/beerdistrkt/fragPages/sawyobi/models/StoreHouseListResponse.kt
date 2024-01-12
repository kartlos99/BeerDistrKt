package com.example.beerdistrkt.fragPages.sawyobi.models

import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.squareup.moshi.Json

data class StoreHouseListResponse(
    @Json(name = "barrels")
    val barrelsIo: List<IoModel>,
    @Json(name = "bottles")
    val bottlesIo: List<BottleIoModel>,
) {
    fun merge(
        beers: List<BeerModelBase>,
        bottles: List<BaseBottleModel>
    ): List<CombinedIoModel> {
        barrelsIo.forEach { ioModel ->
            ioModel.beer = beers.firstOrNull { it.id == ioModel.beerID }
        }
        bottlesIo.forEach { ioModel ->
            ioModel.bottle = bottles.firstOrNull { it.id == ioModel.bottleID }
        }
        val brMap = barrelsIo.groupBy { it.groupID }
        val boMap = bottlesIo.groupBy { it.groupID }
        val result = brMap.map {
            CombinedIoModel(
                it.key,
                it.value[0].ioDate,
                it.value[0].comment,
                it.value,
                boMap[it.key]
            )
        }.toMutableList()
        boMap.forEach {
            if (brMap.keys.contains(it.key).not())
                result.add(
                    CombinedIoModel(
                        it.key,
                        it.value[0].inputDate,
                        it.value[0].comment,
                        bottles = it.value
                    )
                )
        }
        return result.sortedByDescending {
            it.date
        }
    }
}
