package com.example.beerdistrkt.fragPages.sawyobi.domain

import android.util.Log
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.models.bottle.BaseBottleModel

data class StorehouseIO(
    val groupID: String,
    val ioDate: String,
    val distributorID: Int,
    val check: Int,
    val comment: String?,
    val barrelInput: List<BarrelInput>? = null,
    val barrelOutput: List<BarrelOutput>? = null,
    val bottleInput: List<BottleInput>? = null,
) {

    companion object {

        fun fromDto(
            dto: StorehouseIoDto,
            beerList: List<Beer>,
            bottleList: List<BaseBottleModel>
        ): StorehouseIO {

            return StorehouseIO(
                dto.groupID,
                dto.ioDate,
                dto.distributorID,
                dto.check,
                dto.comment,
                barrelInput = dto.barrelInput?.mapNotNull { inputDto ->
                    try {
                        BarrelInput(
                            id = inputDto.id,
                            beer = beerList.firstOrNull { beer -> beer.id == inputDto.beerID }
                                ?: throw NoSuchElementException("there is no beer for ID = ${inputDto.beerID}"),
                            barrelID = inputDto.barrelID,
                            count = inputDto.count
                        )
                    } catch (e: NoSuchElementException) {
//                        TODO notify user about missed item
                        Log.e("KD_", "mapping error: ${e.message}", e)
                        null
                    }
                },
                barrelOutput = dto.barrelOutput?.map { outputDto ->
                    BarrelOutput(
                        id = outputDto.id,
                        barrelID = outputDto.barrelID,
                        count = outputDto.count
                    )
                },
                bottleInput = dto.bottleInput?.mapNotNull { bottleDto ->
                    try {
                        BottleInput(
                            id = bottleDto.id,
                            bottle = bottleList.firstOrNull { bottle -> bottle.id == bottleDto.bottleID }
                                ?: throw NoSuchElementException("there is no bottle for ID = ${bottleDto.bottleID}"),
                            count = bottleDto.count
                        )
                    } catch (e: NoSuchElementException) {
//                        TODO notify user about missed item
                        Log.e("KD_", "mapping error: ${e.message}", e)
                        null
                    }
                },
            )
        }
    }
}

data class BarrelInput(
    val id: Int,
    val beer: Beer,
    val barrelID: Int,
    val count: Int,
)

data class BarrelOutput(
    val id: Int,
    val barrelID: Int,
    val count: Int,
)

data class BottleInput(
    val id: Int,
    val bottle: BaseBottleModel,
    val count: Int,
)