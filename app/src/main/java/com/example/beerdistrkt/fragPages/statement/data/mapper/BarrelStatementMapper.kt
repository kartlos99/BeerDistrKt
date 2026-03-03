package com.example.beerdistrkt.fragPages.statement.data.mapper

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.common.model.BarrelEnum
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.GetBarrelsUseCase
import com.example.beerdistrkt.fragPages.statement.data.model.BarrelStatementDto
import com.example.beerdistrkt.fragPages.statement.data.model.BarrelStatementItemDto
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelIo
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatementItem
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelsBalance
import javax.inject.Inject

class BarrelStatementMapper @Inject constructor(
    private val getBarrelsUseCase: GetBarrelsUseCase,
) {

    var barrels: Map<Int, List<Barrel>>? = null

    suspend fun mapToDomain(dto: BarrelStatementDto): BarrelStatement {
        barrels = getBarrelsUseCase().groupBy { it.id }

        return BarrelStatement(
            totalCount = dto.totalCount,
            firstOperationDate = dto.firstOperationDate,
            statements = groupByDate(dto.statements)
        )
    }

    private fun groupByDate(items: List<BarrelStatementItemDto>): List<BarrelStatementItem> {
        val grouped = items.groupBy { it.dateStr }
        return grouped.flatMap { dtoEntity ->
            buildList {
                mapBarrelItem(dtoEntity.value)?.let(::add)
            }
        }
    }

    private fun mapBarrelItem(dtoItems: List<BarrelStatementItemDto>): BarrelStatementItem? {
        val firstItem = dtoItems.firstOrNull() ?: return null

        return BarrelStatementItem(
            dateStr = firstItem.dateStr,
            balance = BarrelsBalance(
                balance50 = firstItem.b50,
                balance30 = firstItem.b30,
                balance20 = firstItem.b20,
                balance10 = firstItem.b10,
            ),
            ioItems = dtoItems.map {
                BarrelIo(
                    countIn = it.countIn,
                    countOut = it.countOut,
                    barrel = BarrelEnum.findById(it.canType) ?: return null,
                    recId = it.recId,
                )
            },
            comment = dtoItems.mapNotNull { it.comment }
                .distinct()
                .joinToString(COMMENT_SEPARATOR)
        )
    }

    companion object {
        private const val COMMENT_SEPARATOR = " | "
    }
}