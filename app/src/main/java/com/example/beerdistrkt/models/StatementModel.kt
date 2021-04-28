package com.example.beerdistrkt.models

import androidx.room.*
import com.squareup.moshi.Json


data class StatementModel(
    @Json(name = "dt")
    var tarigi: String = "",
    var comment: String? = "",

    var k_in: Float = 0f,
    var k_out: Float = 0f,

    @Json(name = "pr")
    var price: Float = 0f,
    var pay: Float = 0f,

    @Json(name = "bal")
    var balance: Float = 0f,

    var id: Int = 0
)

@Entity(tableName = "obieqts_table")
data class Obieqti(
    @ColumnInfo
    var dasaxeleba: String
) {
    @ColumnInfo
    var adress: String? = null
    @ColumnInfo
    var tel: String? = null
    @ColumnInfo
    var comment: String? = null
    @ColumnInfo
    var sk: String? = null
    @ColumnInfo
    var sakpiri: String? = null
    @ColumnInfo
    var chek: String? = null
    @PrimaryKey
    var id: Int? = null
    @ColumnInfo
    var valiM: Int? = null
    @ColumnInfo
    var valiK30: Int? = null
    @ColumnInfo
    var valiK50: Int? = null

//    var fasebi : FloatArray? = null

//    override fun toString(): String {
//        return "Obieqti(dasaxeleba='$dasaxeleba', adress=$adress, tel=$tel, comment=$comment, sk=$sk, sakpiri=$sakpiri, chek=$chek, id=$id, valiM=$valiM, valiK30=$valiK30, valiK50=$valiK50, fasebi=${fasebi?.contentToString()})"
//    }

    override fun toString(): String {
        return dasaxeleba
    }

    companion object {
        val emptyModel = Obieqti(
            dasaxeleba = "abstract client"
        )
    }
}

data class ObiectWithPrices(
    @Embedded
    val obieqti: Obieqti,
    @Relation(
        parentColumn = "id",
        entityColumn = "objID"
    )
    val prices: List<ObjToBeerPrice>
)