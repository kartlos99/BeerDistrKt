package com.example.beerdistrkt.fragPages.user.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.common.domain.model.EntityStatus

@Keep
data class WorkRegionDto(
    val id: Int,
    val code: String,
    val name: String,
    val status: EntityStatus,
    val ownStorage: Int,
)
