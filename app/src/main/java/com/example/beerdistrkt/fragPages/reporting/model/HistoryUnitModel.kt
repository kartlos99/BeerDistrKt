package com.example.beerdistrkt.fragPages.reporting.model

import com.example.beerdistrkt.utils.DiffItem

data class HistoryUnitModel(
    val hID: String,
    val text: String = "",
    val type: HistoryCellType = HistoryCellType.Regular
): DiffItem

enum class HistoryCellType {
    Header,
    Regular,
    Empty,
    Changed
}