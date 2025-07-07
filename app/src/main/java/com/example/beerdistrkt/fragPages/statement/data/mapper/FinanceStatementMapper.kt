package com.example.beerdistrkt.fragPages.statement.data.mapper

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.GetBarrelsUseCase
import com.example.beerdistrkt.fragPages.statement.data.model.FinanceStatementDto
import com.example.beerdistrkt.fragPages.statement.data.model.FinanceStatementItemDto
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementDetails
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementItem
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import javax.inject.Inject


class FinanceStatementMapper @Inject constructor(
    private val getBarrelsUseCase: GetBarrelsUseCase,
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottlesUseCase: GetBottlesUseCase,
) {

    private var barrelsMap: Map<Int, List<Barrel>>? = null
    private var beersMap: Map<Int, List<Beer>>? = null
    private var bottlesMap: Map<Int, List<Bottle>>? = null

    suspend fun mapToDomain(financeStatementDto: FinanceStatementDto): FinanceStatement {
        barrelsMap = getBarrelsUseCase().groupBy { it.id }
        beersMap = getBeerUseCase().groupBy { it.id }
        bottlesMap = getBottlesUseCase().groupBy { it.id }

        return FinanceStatement(
            totalCount = financeStatementDto.totalCount,
            statements = financeStatementDto.statements.map(::mapFinanceItem)
        )
    }

    fun mapFinanceItem(dtoItem: FinanceStatementItemDto): FinanceStatementItem = with(dtoItem) {
        return FinanceStatementItem(
            dateStr = dateStr,
            price = price,
            pay = pay,
            balance = balance,
            recId = recId,
            recordType = recordType,
            details = parseDetails(details, recordType),
            comment = comment,
        )
    }

    private fun parseDetails(
        details: String?,
        type: StatementRecordType
    ): FinanceStatementDetails? {
        if (details == null) return null
        return when (type) {
            StatementRecordType.SALE_BEER -> {
                val parts = details.split(DELIMITER)
                val count = getIntValue(COUNT_KEY, parts[0])
                val beerId = getIntValue(BEER_KEY, parts[1])
                val barrelId = getIntValue(BARREL_KEY, parts[2])
                val beer = beersMap?.get(beerId)?.firstOrNull() ?: return null
                val barrel = barrelsMap?.get(barrelId)?.firstOrNull() ?: return null

                return FinanceStatementDetails.BeerDetails(
                    count = count,
                    beer = beer,
                    barrel = barrel,
                )
            }

            StatementRecordType.SALE_BOTTLE -> {
                val parts = details.split(DELIMITER)
                val count = getIntValue(COUNT_KEY, parts[0])
                val bottleId = getIntValue(BOTTLE_KEY, parts[1])
                val bottle = bottlesMap?.get(bottleId)?.firstOrNull() ?: return null

                return FinanceStatementDetails.BottleDetails(
                    count = count,
                    bottle = bottle,
                )
            }

            StatementRecordType.TAKE_MONEY,
            StatementRecordType.NONE -> null
        }
    }

    private fun getIntValue(key: String, segment: String): Int {
        return try {
            segment.substring(key.length).toInt()
        } catch (e: Exception) {
            0
        }
    }

    companion object {
        private const val DELIMITER = "|"
        private const val COUNT_KEY = "c:"
        private const val BEER_KEY = "beerId:"
        private const val BOTTLE_KEY = "bottleId:"
        private const val BARREL_KEY = "barrelId:"
    }
}