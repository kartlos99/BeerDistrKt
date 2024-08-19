package com.example.beerdistrkt.fragPages.expense.presentation

sealed class AddExpenseUiEvent {

    data object GoBack: AddExpenseUiEvent()
}