package com.example.beerdistrkt.models

import com.example.beerdistrkt.fragPages.addEditObiects.model.CustomerGroup
import com.example.beerdistrkt.models.bottle.ClientBottlePrice

data class CustomerDataDTO(
    var dasaxeleba: String,
    var group: CustomerGroup = CustomerGroup.BASE,
    var adress: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var sk: String? = null,
    var sakpiri: String? = null,
    var chek: String? = null,
    var id: Int? = null,
    var prices: List<ObjToBeerPrice>,
    var bottlePrices: List<ClientBottlePrice>,
)
