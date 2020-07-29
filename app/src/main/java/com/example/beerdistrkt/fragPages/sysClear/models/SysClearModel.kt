package com.example.beerdistrkt.fragPages.sysClear.models

data class SysClearModel(
    val id: Int,
    val distributorID: Int,
    val passDays: Int,
    val clearDate: String,
    val dasaxeleba: String,
    val comment: String
) {
}