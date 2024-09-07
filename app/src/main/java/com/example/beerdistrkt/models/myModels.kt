package com.example.beerdistrkt.models

import com.squareup.moshi.Json
import java.io.Serializable
import java.util.*

/*
data class OrderCommentRowModel(
    var objName: String,
    var comment: String,
    var comment_Of: String
)
*/


/*
data class SawyobiDetailRow(
    var id: String,
    var tarigi: String,
    var distributor: String,
    var ludi: String,
    var ludisID: Int,
    var comment: String,
    var k30: Float,
    var k50: Float,
    var chek: String
) : Serializable {

    override fun toString(): String {
        return "SawyobiDetailRow{" +
                "tarigi='" + tarigi + '\'' +
                ", distributor='" + distributor + '\'' +
                ", ludi='" + ludi + '\'' +
                ", comment='" + comment + '\'' +
                ", chek='" + chek + '\'' +
                ", id='" + id + '\'' +
                ", k30=" + k30 +
                ", k50=" + k50 +
                '}'
    }
}

class Shekvetebi(
    var obieqti: String,
    var ludi: String,
    var k30in: Float,
    var k50in: Float,
    var k30wont: Float,
    var k50wont: Float
) : Serializable {
    var tarigi: String? = null
    var chk: String? = null
    var distrib_Name: String? = null
    var comment: String? = null
    var color: String? = null
    var order_id = 0
    var distrib_id = 0
    var beer_id = 0

    override fun toString(): String {
        return "Shekvetebi{" +
                "obieqti='" + obieqti + '\'' +
                ", ludi='" + ludi + '\'' +
                ", tarigi='" + tarigi + '\'' +
                ", chk='" + chk + '\'' +
                ", distrib_Name='" + distrib_Name + '\'' +
                ", comment='" + comment + '\'' +
                ", color='" + color + '\'' +
                ", order_id=" + order_id +
                ", distrib_id=" + distrib_id +
                ", beer_id=" + beer_id +
                ", k30in=" + k30in +
                ", k50in=" + k50in +
                ", k30wont=" + k30wont +
                ", k50wont=" + k50wont +
                '}'
    }

    fun addK30(color: String?) {
        this.color = color
    }

}
*/

/*
data class ShekvetebiGR(
    var name: String,
    var childs: ArrayList<Shekvetebi>
) : Serializable {
    var grHeadOrderSum: ArrayList<ShekvetebiSum> =
        ArrayList<ShekvetebiSum>()
    var k30w = 0f
    var k50w = 0f
    var k30 = 0f
    var k50 = 0f
}
*/

/*
class ShekvetebiSum : Serializable {
    var ludi: String? = null
    var distrib_Name: String? = null
    var distrib_id = 0
    var k30in = 0f
    var k50in = 0f
    var k30wont = 0f
    var k50wont = 0f
}

class Totalinout(
    var ludi: String,
    var k30s: Float,
    var k50s: Float,
    var k30r: Float,
    var k50r: Float
) :
    Serializable
*/

class Xarji(
    var comment: String,
    @Json(name = "distributor_id")
    var distrID: String,
    var id: String,
    @Json(name = "tanxa")
    var amount: Float
) {

    override fun toString(): String {
        return "Xarji{" +
                "comment='" + comment + '\'' +
                ", distrID='" + distrID + '\'' +
                ", id='" + id + '\'' +
                ", amount=" + amount +
                '}'
    }
}

data class DeleteRequest(
    val recordID: String,
    val table: String,
    val userID: String
)

/*
data class SimpleResponce(
    val result: String,
    val error: String? = null,
    val data: String? = null,
    val post: String? = null
)
*/