package com.example.beerdistrkt.fragPages.sawyobi.models

data class SimpleBeerRowModel(
    val title: String = "",
    val values: Map<Int, Int>,
    val middleIconRes: Int? = null,
    val iconColor: Int? = null,
    val underlineColor: Int? = null
)