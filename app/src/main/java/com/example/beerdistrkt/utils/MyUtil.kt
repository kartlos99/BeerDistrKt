package com.example.beerdistrkt.utils

import android.content.Context
import com.example.beerdistrkt.models.Shekvetebi
import java.text.DecimalFormat
import java.util.*

class MyUtil {

    private val SHARED_PREF = "my_pref"
    private val DATA_KEY = "datakey"
    private var mContext: Context? = null

    fun MyUtil(context: Context?) {
        mContext = context
    }
    companion object{

        private val df = DecimalFormat("#0.00")

        fun pliusMinusText(
            stringNumber: String,
            oper: String
        ): String? { // oper   (true = +) (false = -)
            var stringNumber = stringNumber
            if (stringNumber == "") {
                stringNumber = "0"
            }
            var ii = java.lang.Float.valueOf(stringNumber)
            if (oper == "+") {
                ii++
            } else {
                if (ii >= 1) {
                    ii--
                }
            }
            return floatToSmartStr(ii)
        }

        fun floatToSmartStr(number: Float): String? {
            val intNumb = Math.round(number).toLong()
            return if (Math.abs(number - intNumb) < ACCURACY) {
                intNumb.toString()
            } else df.format(number)
        }
    }




//    fun tempListPrice(tempList: ArrayList<Shekvetebi>): Float {
//        var n = 0f
//        for (tempItem in tempList) {
//            n += (tempItem.getK30in() * 30 + tempItem.getK50in() * 50) * java.lang.Float.valueOf(
//                tempItem.getComment()
//            )
//        }
//        return n
//    }
//
//    fun totalXarji(xarjebi: ArrayList<Xarji>): Float {
//        var n = 0f
//        for (xarji in xarjebi) {
//            n += xarji.getAmount()
//        }
//        return n
//    }
//
//    fun getUserName(id: Int): String? {
//        for (user in Constantebi.USERsLIST) {
//            if (user.getId() === id) {
//                return user.getUsername()
//            }
//        }
//        return ""
//    }
//
//    fun notifyFirebase() {
//        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//        val myMsgRef: DatabaseReference =
//            database.getReference(mContext!!.getString(R.string.location_en))
//        val date = Date()
//        myMsgRef.setValue(date.toString())
//        saveLastValue(date.toString())
//    }

    fun saveLastValue(data: String?) {
        val sharedPreferences =
            mContext!!.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(DATA_KEY, data)
        editor.apply()
    }

    fun loadLastValue(): String? {
        val sharedPreferences =
            mContext!!.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return sharedPreferences.getString(DATA_KEY, "")
    }
}