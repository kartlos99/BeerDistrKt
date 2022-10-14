package com.example.beerdistrkt.models

data class CustomerDataDTO(
    var dasaxeleba: String,
    var adress: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var sk: String? = null,
    var sakpiri: String? = null,
    var chek: String? = null,
    var id: Int? = null,
    var prices: List<ObjToBeerPrice>
)
