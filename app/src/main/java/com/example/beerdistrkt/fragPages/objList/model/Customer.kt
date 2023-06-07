package com.example.beerdistrkt.fragPages.objList.model

import com.example.beerdistrkt.models.CustomerIdlInfo

data class Customer(
    var id: Int? = null,
    var dasaxeleba: String,
    var adress: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var sk: String? = null,
    var sakpiri: String? = null,
    var chek: String? = null,
    var warnInfo: CustomerIdlInfo? = null
)