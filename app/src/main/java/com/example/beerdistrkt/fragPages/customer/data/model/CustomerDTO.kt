package com.example.beerdistrkt.fragPages.customer.data.model

import com.example.beerdistrkt.fragPages.customer.domain.model.CustomerGroup
import com.example.beerdistrkt.models.ObjToBeerPrice
import com.example.beerdistrkt.models.bottle.ClientBottlePrice

data class CustomerDTO(
    var dasaxeleba: String,
    var group: CustomerGroup = CustomerGroup.BASE,
    var address: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var sk: String? = null,
    var sakpiri: String? = null,
    var chek: String? = null,
    var id: Int? = null,
    var prices: List<ObjToBeerPrice>,
    var bottlePrices: List<ClientBottlePrice>,
)
