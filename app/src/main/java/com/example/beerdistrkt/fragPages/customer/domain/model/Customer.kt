package com.example.beerdistrkt.fragPages.customer.domain.model

import com.example.beerdistrkt.common.domain.model.EntityStatus
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
    val status: EntityStatus = EntityStatus.ACTIVE,
    var hasCheck: Boolean = false,
    var warnInfo: CustomerIdlInfo? = null,
    var group: CustomerGroup = CustomerGroup.BASE,
    val beerPrices: List<ClientBeerPrice> = emptyList(),
    val bottlePrices: List<ClientBottlePrice> = emptyList(),
) {

    fun isActive(): Boolean = status == EntityStatus.ACTIVE

    fun toObieqti(): Obieqti {
        val ob = Obieqti(name)
        ob.group = group
        ob.id = id
        ob.adress = address
        ob.tel = tel
        ob.comment = comment
        ob.sk = identifyCode
        ob.sakpiri = contactPerson
        ob.chek = hasCheck.toString()
        return ob
    }
}