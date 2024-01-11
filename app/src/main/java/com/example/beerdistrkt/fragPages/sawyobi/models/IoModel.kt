package com.example.beerdistrkt.fragPages.sawyobi.models

import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.TempBeerItemModel

data class IoModel(
    val ID: Int,
    val groupID: String,
    val ioDate: String,
    val distributorID: Int,
    val beerID: Int,
    val barrelID: Int,
    val count: Int,
    val chek: Int,
    val comment: String?,
) {
    fun toTempBeerItemModel(
        barrels: List<CanModel>,
        beerList: List<BeerModelBase>,
        onRemove: (beerItem: TempBeerItemModel) -> Unit,
        onEdit: (beerItem: TempBeerItemModel) -> Unit
    ): TempBeerItemModel {
        val beerMap = beerList.groupBy { it.id }.mapValues { it.value[0] }
        val barrelMap = barrels.groupBy { it.id }.mapValues { it.value[0] }
        return TempBeerItemModel(
            ID = ID,
            beer = beerMap[beerID] ?: error("no beer match"),
            canType = barrelMap[barrelID] ?: error("no barrel match"),
            count = count,
            orderItemID = ID,
            onRemoveClick = onRemove,
            onEditClick = onEdit
        )
    }
}