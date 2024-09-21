package com.example.beerdistrkt.fragPages.realisationtotal.domain.model

import com.example.beerdistrkt.utils.DiffItem

data class BottleSale(
    val id: Int,
    val name: String,
    val price: Double,
    val count: Int,
): DiffItem
