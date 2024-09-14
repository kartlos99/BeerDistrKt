package com.example.beerdistrkt.fragPages.realisationtotal.data

import com.example.beerdistrkt.fragPages.expense.data.ExpenseMapper
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.realisationtotal.data.model.RealizationDayDto
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import javax.inject.Inject

class RealizationDayMapper @Inject constructor(
    private val expenseMapper: ExpenseMapper,
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
) {

    fun map(data: RealizationDayDto): RealizationDay {
        return RealizationDay(
            data.sale,
            data.takenMoney,
            data.barrels,
            data.expenses.map {
                expenseMapper.mapToDomain(it, getExpenseCategoriesUseCase.categories)
            }
        )
    }
}