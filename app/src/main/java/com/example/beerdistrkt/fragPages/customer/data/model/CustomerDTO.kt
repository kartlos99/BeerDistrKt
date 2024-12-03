package com.example.beerdistrkt.fragPages.customer.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import com.example.beerdistrkt.fragPages.customer.domain.model.CustomerGroup
import com.example.beerdistrkt.models.ObjToBeerPrice

@Keep
data class CustomerDTO(
    var id: Int? = null,
    var name: String,
    var group: CustomerGroup = CustomerGroup.BASE,
    var address: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var identifyCode: String? = null,
    var contactPerson: String? = null,
    var chek: String? = null,
    var beerPrices: List<ObjToBeerPrice>,
    var bottlePrices: List<ClientBottlePrice>,
)
