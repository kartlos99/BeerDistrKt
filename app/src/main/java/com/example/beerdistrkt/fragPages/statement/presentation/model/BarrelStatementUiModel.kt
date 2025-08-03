package com.example.beerdistrkt.fragPages.statement.presentation.model

data class BarrelStatementUiModel(
    val dateStr: String,
    val comment: String?,
    val recordId: Long?,
    val balance50: Int,
    val balance30: Int,
    val balance20: Int,
    val balance10: Int,
    val inOut50: Pair<Int, Int>,
    val inOut30: Pair<Int, Int>,
    val inOut20: Pair<Int, Int>,
    val inOut10: Pair<Int, Int>,
    val details: String? = null,
    val color: Int? = null,
)
