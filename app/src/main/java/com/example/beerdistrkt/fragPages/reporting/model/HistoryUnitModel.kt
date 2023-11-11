package com.example.beerdistrkt.fragPages.reporting.model

import com.example.beerdistrkt.utils.DiffItem
import java.util.UUID

data class HistoryUnitModel(
    val text: String = "",
    val type: HistoryCellType = HistoryCellType.Regular
) : DiffItem {
    override val key: Any
        get() = UUID.randomUUID()

    companion object {
        val EMPTY = HistoryUnitModel("", HistoryCellType.Empty)
    }
}

enum class HistoryCellType {
    Header,
    Regular,
    Empty,
    Changed
}