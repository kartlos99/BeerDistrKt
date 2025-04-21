package com.example.beerdistrkt.fragPages.customer.presentation.model

import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.customer.domain.model.CustomerGroup
import com.example.beerdistrkt.models.CustomerIdlInfo

data class CustomerUiModel(
    val id: Int? = null,
    val name: String = String.empty(),
    val address: String = String.empty(),
    val tel: String = String.empty(),
    val comment: String = String.empty(),
    val identifyCode: String = String.empty(),
    val contactPerson: String = String.empty(),
    val status: EntityStatus = EntityStatus.ACTIVE,
    val hasCheck: Boolean = false,
    val warnInfo: CustomerIdlInfo? = null,
    val group: CustomerGroup = CustomerGroup.BASE,
    val beerPrices: List<PriceEditModel> = emptyList(),
    val bottlePrices: List<PriceEditModel> = emptyList(),
)
