package com.example.beerdistrkt.fragPages.sawyobi.domain

import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.utils.DiffItem

data class StorehouseIO(
    val groupID: String,
    val ioDate: String,
    val distributorID: Int,
    val check: Int,
    val comment: String?,
    val barrelInput: List<BarrelInput>? = null,
    val barrelOutput: List<BarrelOutput>? = null,
    val bottleInput: List<BottleInput>? = null,
): DiffItem {

    override val key: String
        get() = groupID

    companion object {

        fun fromDto(dto: StorehouseIoDto): StorehouseIO {
            return StorehouseIO(
                dto.groupID,
                dto.ioDate,
                dto.distributorID,
                dto.check,
                dto.comment,
            )
        }
    }
}

data class BarrelInput(
    val id: Int,
    val beer: BeerModelBase,
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