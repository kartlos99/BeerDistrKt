package com.example.beerdistrkt.fragPages.reporting.repo

import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryCellType
import com.example.beerdistrkt.fragPages.reporting.model.HistoryDto
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class HistoryItemMapper {

    private val workScope = CoroutineScope(Dispatchers.Default)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    fun map(
        data: HistoryDto,
        tableName: DbTableName
    ): List<HistoryUnitModel> {
        val result: MutableList<HistoryUnitModel> =
            mutableListOf(HistoryUnitModel.EMPTY)

        tableName.headerItems.forEach { title ->
            result.add(HistoryUnitModel(title, HistoryCellType.Header))
        }
        result.addAll(transformList(mapDtoData(data), tableName))
        return result
    }

    private fun mapDtoData(dataDto: HistoryDto) = dataDto.history.map {
        val resultMap = mutableMapOf<String, String>()
        it.entries.forEach { entry ->
            val value = when (entry.key) {
                COLUMN_CUSTOMER_ID -> dataDto.customers?.get(entry.value) ?: ""
                TABLE_COLUMN_MODIFY_USER_ID,
                COLUMN_DISTRIBUTOR_ID -> dataDto.users?.get(entry.value) ?: ""

                COLUMN_BARREL_ID -> dataDto.barrels?.get(entry.value) ?: ""
                else -> entry.value
            }
            resultMap[entry.key] = value
        }
        resultMap.toMap()
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
                "${record[TABLE_COLUMN_MODIFY_DATE]}\n${record[TABLE_COLUMN_MODIFY_USER_ID]}",
                HistoryCellType.Header
            ),
        )
        tableName.visibleKeys.forEach {
            val previousItem = if (prev == null) null else prev[it] ?: ""
            result.add(makeHistUnit(record[it] ?: "", previousItem, isLast))
        }
        return result
    }

    private fun makeHistUnit(
        current: String,
        prev: String?,
        isLast: Boolean = false
    ): HistoryUnitModel {
        val type = when {
            prev == null -> HistoryCellType.Regular
            current != prev -> HistoryCellType.Changed
            isLast -> HistoryCellType.Regular
            else -> HistoryCellType.Empty
        }
        return HistoryUnitModel(current, type)
    }

    companion object {
        const val TABLE_COLUMN_MODIFY_USER_ID = "modifyUserID"
        const val TABLE_COLUMN_MODIFY_DATE = "modifyDate"
        const val COLUMN_CUSTOMER_ID = "clientID"
        const val COLUMN_DISTRIBUTOR_ID = "distributorID"
        const val COLUMN_BARREL_ID = "canTypeID"
    }
}