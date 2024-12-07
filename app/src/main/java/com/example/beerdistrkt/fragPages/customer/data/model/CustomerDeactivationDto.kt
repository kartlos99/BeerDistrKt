package com.example.beerdistrkt.fragPages.customer.data.model

import androidx.annotation.Keep

@Keep
data class CustomerDeactivationDto (
    val customerID: Int,
    val allRegions: Boolean = false
)