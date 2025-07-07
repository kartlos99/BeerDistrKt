package com.example.beerdistrkt.fragPages.statement.data.mapper

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.GetBarrelsUseCase
import com.example.beerdistrkt.fragPages.statement.data.model.BarrelStatementDto
import com.example.beerdistrkt.fragPages.statement.data.model.BarrelStatementItemDto
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatementItem
import javax.inject.Inject

class BarrelStatementMapper @Inject constructor(
    private val getBarrelsUseCase: GetBarrelsUseCase,
) {

    var barrels: Map<Int, List<Barrel>>? = null

    suspend fun mapToDomain(dto: BarrelStatementDto): BarrelStatement {
        barrels = getBarrelsUseCase().groupBy { it.id }

        return BarrelStatement(
            totalCount = dto.totalCount,
            statements = dto.statements.mapNotNull(::mapBarrelItem)
        )
    }

    fun mapBarrelItem(dtoItem: BarrelStatementItemDto): BarrelStatementItem? = with(dtoItem) {
        val barrel = barrels?.get(canType)?.firstOrNull() ?: return null

        return BarrelStatementItem(
            dateStr = dateStr,
            countIn = countIn,
            countOut = countOut,
            barrel = barrel,
            balance50 = b50,
            balance30 = b30,
            balance20 = b20,
            balance10 = b10,
            recId = recId,
            comment = comment
        )
    }
}