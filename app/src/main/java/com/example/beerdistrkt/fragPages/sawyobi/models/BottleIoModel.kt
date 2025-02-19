package com.example.beerdistrkt.fragPages.sawyobi.models

import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.presentation.model.TempBottleItemModel

data class BottleIoModel(
    val id: Int,
    val regionID: Int,
    val groupID: String,
    val inputDate: String,
    val distributorID: Int,
    val bottleID: Int,
    val count: Int,
    val chek: Int,
    val comment: String?,
) {
    var bottle: Bottle? = null

    fun toTempBottleItemModel(
        bottles: List<Bottle>,
        onRemove: (beerItem: TempBottleItemModel) -> Unit,
        onEdit: (beerItem: TempBottleItemModel) -> Unit
    ): TempBottleItemModel {
        val bottle =
            bottles.firstOrNull { it.id == bottleID } ?: error("bottle match error in storehouse!")
        return TempBottleItemModel(
            id = id,
            bottle = bottle,
            count = count,
            onRemove,
            orderItemID = id,
            onEdit
        )
    }
}