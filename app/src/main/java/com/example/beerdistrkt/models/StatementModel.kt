package com.example.beerdistrkt.models

import android.annotation.SuppressLint
import androidx.room.*
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.K_OUT
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.MITANA
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.M_OUT
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.*

data class StatementResponse(
    val totalCount: Int,
    val list: List<StatementModel>
)

data class StatementModel(
    @Json(name = "dt")
    var tarigi: String = "",
    var comment: String? = "",

    var k_in: Int = 0,
    var k_out: Int = 0,

    @Json(name = "pr")
    var price: Float = 0f,
    var pay: Float = 0f,

    @Json(name = "bal")
    var balance: Float = 0f,

    var id: Int = 0
) {
    @SuppressLint("SimpleDateFormat")
    fun getItemDate(pattern: String): Date? {
        return SimpleDateFormat(pattern).parse(tarigi)
    }

    fun getType(location: Int): String = when {
        location == 0 && pay != 0F -> M_OUT
        location == 1 && k_out != 0 -> K_OUT
        else -> MITANA
    }

}

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