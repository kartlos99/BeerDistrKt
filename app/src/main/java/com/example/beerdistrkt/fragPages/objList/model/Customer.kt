package com.example.beerdistrkt.fragPages.objList.model

import com.example.beerdistrkt.models.CustomerIdlInfo
import com.example.beerdistrkt.models.Obieqti

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
) {
    fun toObieqti(): Obieqti {
        val ob = Obieqti(dasaxeleba)
        ob.id = id
        ob.adress = adress
        ob.tel = tel
        ob.comment = comment
        ob.sk = sk
        ob.sakpiri = sakpiri
        ob.chek = chek
        return ob
    }
}