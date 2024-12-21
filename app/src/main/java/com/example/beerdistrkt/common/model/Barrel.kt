package com.example.beerdistrkt.common.model

import com.example.beerdistrkt.common.domain.model.EntityStatus

data class Barrel(
    val id: Int,
    val name: String,
    val volume: Int,
    val status: EntityStatus,
    val sortValue: String,
    val image: String?,
)
