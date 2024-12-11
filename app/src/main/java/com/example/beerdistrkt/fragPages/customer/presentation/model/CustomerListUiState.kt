package com.example.beerdistrkt.fragPages.customer.presentation.model

import com.example.beerdistrkt.fragPages.customer.domain.model.Customer

data class CustomerListUiState(
    val customers: List<Customer>,
    val isFiltered: Boolean = false,
)
