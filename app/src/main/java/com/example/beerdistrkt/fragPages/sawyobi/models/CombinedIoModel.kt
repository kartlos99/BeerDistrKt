package com.example.beerdistrkt.fragPages.sawyobi.models

data class CombinedIoModel(
    val groupID: String,
    val date: String,
    val comment: String? = null,
    val barrels: List<IoModel>? = null,
    val bottles: List<BottleIoModel>? = null,
)