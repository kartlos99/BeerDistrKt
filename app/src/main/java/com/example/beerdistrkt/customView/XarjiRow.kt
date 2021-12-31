package com.example.beerdistrkt.customView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.Xarji
import com.example.beerdistrkt.utils.MSG_DEL

class XarjiRowView @JvmOverloads constructor(
    private var mContext: Context,
    private var xarji: Xarji,
    private var xarjiAuthor: String,
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
        tComment.text = xarji.comment
        tAmount.text = mContext.getString(R.string.format_gel, xarji.amount)
        tDistr.text = xarjiAuthor
    }

    val view: View
        get() = this

    companion object {
        private const val TAG = "XarjiRowTAG"
    }
}