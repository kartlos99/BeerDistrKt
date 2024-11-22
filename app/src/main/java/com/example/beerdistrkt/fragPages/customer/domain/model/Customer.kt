package com.example.beerdistrkt.fragPages.customer.domain.model

import com.example.beerdistrkt.models.CustomerIdlInfo
import com.example.beerdistrkt.models.Obieqti

data class Customer(
    var id: Int? = null,
    var name: String,
    var address: String? = null,
    var tel: String? = null,
    var comment: String? = null,
    var identifyCode: String? = null,
    var contactPerson: String? = null,
    var chek: String? = null,
    var warnInfo: CustomerIdlInfo? = null,
    var group: CustomerGroup = CustomerGroup.BASE,
    val beerPrices: List<ClientBeerPrice> = emptyList(),
    val bottlePrices: List<ClientBottlePrice> = emptyList(),
) {
    fun toObieqti(): Obieqti {
        val ob = Obieqti(name)
        ob.group = group
        ob.id = id
        ob.adress = address
        ob.tel = tel
        ob.comment = comment
        ob.sk = identifyCode
        ob.sakpiri = contactPerson
        ob.chek = chek
        return ob
    }
}