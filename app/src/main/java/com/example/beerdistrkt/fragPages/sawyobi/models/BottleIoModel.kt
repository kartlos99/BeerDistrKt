package com.example.beerdistrkt.fragPages.sawyobi.models

import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel

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
    var bottle: BaseBottleModel? = null

    fun toTempBottleItemModel(
        bottles: List<BaseBottleModel>,
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