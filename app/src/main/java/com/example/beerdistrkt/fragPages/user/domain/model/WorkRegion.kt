package com.example.beerdistrkt.fragPages.user.domain.model

import com.example.beerdistrkt.common.domain.model.EntityStatus

data class WorkRegion(
    val id: Int,
    val code: String,
    val name: String,
    val status: EntityStatus,
    val hasOwnStorage: Boolean,
)
