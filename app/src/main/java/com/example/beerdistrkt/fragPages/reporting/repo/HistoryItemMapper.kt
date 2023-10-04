package com.example.beerdistrkt.fragPages.reporting.repo

import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryCellType
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel

class HistoryItemMapper {

    fun map(data: List<Map<String, String>>, tableName: DbTableName): List<HistoryUnitModel> {
        val result: MutableList<HistoryUnitModel> =
            mutableListOf(HistoryUnitModel("", "", HistoryCellType.Empty))

        tableName.headerItems.forEach { title ->
            result.add(HistoryUnitModel(title, title, HistoryCellType.Header))
        }

        result.addAll(transformList(data, tableName))
        return result
    }

    private fun transformList(
        dataList: List<Map<String, String>>,
        tableName: DbTableName
    ): Collection<HistoryUnitModel> {
        val result: MutableList<HistoryUnitModel> = mutableListOf()

        dataList.forEachIndexed { index, barrelOutputRecord ->
            val isLast = index == dataList.size - 1
            val previousRecord = if (index == 0) null else dataList[index - 1]
            result.addAll(
                transformRecord(barrelOutputRecord, previousRecord, isLast, tableName)
            )
        }
        return result
    }

    private fun transformRecord(
        record: Map<String, String>,
        prev: Map<String, String>?,
        isLast: Boolean = false,
        tableName: DbTableName
    ): Collection<HistoryUnitModel> {

        val result = mutableListOf(
            HistoryUnitModel(
                record.get("hid") ?: "",
                "${record["modifyDate"]}\n${record["modifyUserID"]}",
                HistoryCellType.Header
            ),
        )
        tableName.visibleKeys.forEach {
            val previousItem = if (prev == null) null else prev[it] ?: ""
            result.add(makeHistUnit(record["hid"] ?: "", record[it] ?: "", previousItem, isLast))
        }
        return result
    }

    private fun makeHistUnit(
        id: String,
        current: String,
        prev: String?,
        isLast: Boolean = false
    ): HistoryUnitModel {
        val value = current.takeIf { prev == null || current != prev } ?: ""
        val type = when {
            prev == null -> HistoryCellType.Regular
            current != prev -> HistoryCellType.Changed
            isLast -> HistoryCellType.Regular
            else -> HistoryCellType.Empty
        }
        return HistoryUnitModel(id, value, type)
    }
}