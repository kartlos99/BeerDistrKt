package com.example.beerdistrkt.fragPages.statement.presentation.adapter

import com.example.beerdistrkt.fragPages.statement.presentation.model.FStatementUiItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.SaleItemUiModel

interface FStatementActionListener {
    fun onPaymentOptionClick(item: FStatementUiItem.Money)
    fun onSaleOptionClick(item: SaleItemUiModel)
}