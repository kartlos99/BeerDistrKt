package com.example.beerdistrkt.fragPages.statement.data.mapper

import com.example.beerdistrkt.fragPages.statement.data.model.FinanceStatementDto
import com.example.beerdistrkt.fragPages.statement.data.model.FinanceStatementItemDto
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementItem
import javax.inject.Inject


class FinanceStatementMapper @Inject constructor() {

    fun mapToDomain(financeStatementDto: FinanceStatementDto): FinanceStatement {
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
            details = null, // TODO("parse concatenated data to an object")
            comment = comment
        )
    }
}