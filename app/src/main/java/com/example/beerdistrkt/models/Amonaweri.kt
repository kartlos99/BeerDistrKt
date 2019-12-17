package com.example.beerdistrkt.models


data class Amonaweri(
    var tarigi: String? = null,
    var comment: String? = null,
    var k_in: Float = 0f,
    var k_out: Float = 0f,
    var k_balance: Float = 0f,
    var price: Float = 0f,
    var pay: Float = 0f,
    var balance: Float = 0f,
    var id: Int = 0
)

data class Obieqti(var dasaxeleba: String) {
    var adress: String? = null
    var tel: String? = null
    var comment: String? = null
    var sk: String? = null
    var sakpiri: String? = null
    var chek: String? = null
    var id: Int? = null
    var valiM: Int? = null
    var valiK30: Int? = null
    var valiK50: Int? = null
    var fasebi : FloatArray? = null

//    override fun toString(): String {
//        return "Obieqti(dasaxeleba='$dasaxeleba', adress=$adress, tel=$tel, comment=$comment, sk=$sk, sakpiri=$sakpiri, chek=$chek, id=$id, valiM=$valiM, valiK30=$valiK30, valiK50=$valiK50, fasebi=${fasebi?.contentToString()})"
//    }

    override fun toString(): String {
        return dasaxeleba
    }

}