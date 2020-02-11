package com.example.beerdistrkt.customView

import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sales.SalesViewModel
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.models.Xarji
import com.example.beerdistrkt.utils.MSG_DEL
import com.example.beerdistrkt.utils.Session
import java.security.acl.Owner
import java.util.*

class XarjiRowView(
    private var mContext: Context? = null,
    private var xarji: Xarji,
    private var xarjiAuthor: String,
    private var xarjebi: ArrayList<Xarji>? = null,
    private var linearConteiner: LinearLayout? = null,
//    private var tShowSum: TextView? = null,
    private var canDel: Boolean? = null,
    callback: (id: String) -> Unit
) : ConstraintLayout(mContext) {

    private var btnItemRemove: ImageButton? = null

    init {
        initView()
        btnItemRemove?.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setMessage(MSG_DEL)
            builder.setPositiveButton("დიახ") { dialogInterface, i ->
                Log.d(TAG, "start Deleting")
                callback(xarji.id)
            }
                .setNegativeButton("არა") { dialogInterface, i -> }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }

    private fun initView() {
        val thisRootView: ConstraintLayout = LayoutInflater.from(mContext).inflate(
            R.layout.xarji_row,
            this,
            true
        ) as ConstraintLayout

        val tComment: TextView = thisRootView.findViewById(R.id.t_xarj_row_comment)
        val tDistr: TextView = thisRootView.findViewById(R.id.t_xarj_user)
        val tAmount: TextView = thisRootView.findViewById(R.id.t_xarj_row_amount)
        btnItemRemove = thisRootView.findViewById(R.id.btn_xarj_remove)
        if (!canDel!!) btnItemRemove!!.visibility = View.GONE
        tComment.setText(xarji.comment)
        tAmount.setText(mContext!!.getString(R.string.format_gel, xarji.amount))
        tDistr.setText(xarjiAuthor)
    }

    val view: View
        get() = this

    companion object {
        private const val TAG = "XarjiRowTAG"
    }
}