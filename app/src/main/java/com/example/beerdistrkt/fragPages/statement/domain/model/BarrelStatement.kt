package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.common.model.Barrel

data class BarrelStatement(
    val totalCount: Long,
    val statements: List<BarrelStatementItem>,
)

data class BarrelStatementItem(
    val dateStr: String,
    val countIn: Int,
    val countOut: Int,
    val barrel: Barrel,
    val balance50: Int,
    val balance30: Int,
    val balance20: Int,
    val balance10: Int,
    val recId: Long,
    val comment: String?,
)