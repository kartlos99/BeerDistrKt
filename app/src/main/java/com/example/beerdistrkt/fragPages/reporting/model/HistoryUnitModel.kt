package com.example.beerdistrkt.fragPages.reporting.model

import com.example.beerdistrkt.utils.DiffItem
import java.util.UUID

data class HistoryUnitModel(
    val hID: String,
    val text: String = "",
    val type: HistoryCellType = HistoryCellType.Regular
) : DiffItem {
    override val key: Any
        get() = UUID.randomUUID()
}

enum class HistoryCellType {
    Header,
    Regular,
    Empty,
    Changed
}