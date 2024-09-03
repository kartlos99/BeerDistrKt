package com.example.beerdistrkt.fragPages.expensecategory.presentation

data class UiState(
    val error: String? = null,
    val successResult: SuccessOf? = null,
)

sealed class SuccessOf {
    data object UPDATE: SuccessOf()
    data object DELETE: SuccessOf()
}