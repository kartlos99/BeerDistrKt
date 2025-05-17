package com.example.beerdistrkt.fragPages.customer.domain.model

import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.models.CustomerIdlInfo

data class Customer(
    var id: Int? = null,
    var name: String,
    var address: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var identifyCode: String? = null,
    var contactPerson: String? = null,
    val location: String? = null,
    val paymentType: PaymentType? = null,
    val status: EntityStatus = EntityStatus.ACTIVE,
    var hasCheck: Boolean = false,
    var warnInfo: CustomerIdlInfo? = null,
    var group: CustomerGroup = CustomerGroup.BASE,
    val beerPrices: List<ClientBeerPrice> = emptyList(),
    val bottlePrices: List<ClientBottlePrice> = emptyList(),
) {

    fun isActive(): Boolean = status == EntityStatus.ACTIVE

}