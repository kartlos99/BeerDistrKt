package com.example.beerdistrkt.fragPages.sawyobi.models

data class StoreInsertRequestModel(
    val comment: String?,
    val groupID: String,
    val chek: Int,
    val operationTime: String = "",
    val inputBeer: List<ReceiveItem>? = null,
    val inputBottle: List<ReceiveBottleItem>? = null,
    val outputBarrels: List<BarrelOutItem>? = null
) {

    data class ReceiveItem(
        val ID: Int = 0,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int
    )

    data class ReceiveBottleItem(
        val ID: Int = 0,
        val bottleID: Int,
        val count: Int
    )

    data class BarrelOutItem(
        val ID: Int = 0,
        val canTypeID: Int,
        val count: Int
    )
}