package com.example.beerdistrkt.fragPages.sawyobi.models

data class StoreInsertRequestModel(
    val comment: String?,
    val modifyUserID: Int,
    val chek: Int,

    val inputBeer: List<ReceiveItem>? = null,
    val outputBarrels: List<BarrelOutItem>? = null
) {

    data class ReceiveItem(
        val ID: Int = 0,
        val receiveDate: String? = null,
        val beerID: Int,
        val canTypeID: Int,
        val count: Int
    )

    data class BarrelOutItem(
        val ID: Int = 0,
        val outputDate: String? = null,
        val canTypeID: Int,
        val count: Int
    )
}