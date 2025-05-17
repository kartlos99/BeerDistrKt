package com.example.beerdistrkt.fragPages.customer.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import com.example.beerdistrkt.fragPages.customer.domain.model.CustomerGroup
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.models.ObjToBeerPrice

@Keep
data class CustomerDTO(
    val id: Int? = null,
    val name: String,
    val group: CustomerGroup = CustomerGroup.BASE,
    val address: String? = null,
    val tel: String? = null,
    val comment: String? = null,
    val identifyCode: String? = null,
    val contactPerson: String? = null,
    val location: String? = null,
    val paymentType: PaymentType? = null,
    val status: EntityStatus,
    val chek: String? = null,
    val beerPrices: List<ObjToBeerPrice>,
    val bottlePrices: List<ClientBottlePrice>,
)
