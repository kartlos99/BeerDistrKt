package com.example.beerdistrkt.fragPages.user.presentation.model

import com.example.beerdistrkt.fragPages.user.domain.model.User


data class ModifyUserData(
    val user: User? = null,
    val canModifyRegion: Boolean = false,
    val regionChips: List<RegionChipItem> = emptyList(),
)
