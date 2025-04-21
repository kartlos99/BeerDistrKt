package com.example.beerdistrkt.fragPages.expense.data

import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseDto
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUserUseCase
import javax.inject.Inject

class ExpenseMapper @Inject constructor(
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
    private val getUserUseCase: GetUserUseCase,
) {

    suspend fun mapToDomain(
        expenseDto: ExpenseDto,
    ): Expense {
        return Expense(
            id = expenseDto.id,
            distributor = getUserUseCase(expenseDto.distributorID)
                ?: throw NoSuchElementException(NO_MATCHING_USER),
            amount = expenseDto.amount,
            comment = expenseDto.comment,
            date = expenseDto.date,
            category = getExpenseCategoriesUseCase.categories.firstOrNull {
                it.id == expenseDto.category
            } ?: throw NoSuchElementException(NO_MATCHING_CATEGORY)
        )
    }

    companion object {
        private const val NO_MATCHING_CATEGORY = "expense category can't be found!"
        private const val NO_MATCHING_USER = "expense author can't be found!"
    }
}