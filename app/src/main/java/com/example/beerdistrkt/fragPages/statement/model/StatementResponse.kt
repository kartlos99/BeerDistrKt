package com.example.beerdistrkt.fragPages.statement.model

data class StatementResponse(
    val totalCount: Int,
    val list: List<StatementModel>
)