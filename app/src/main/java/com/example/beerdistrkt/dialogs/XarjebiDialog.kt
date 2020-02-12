package com.example.beerdistrkt.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.beerdistrkt.R

class XarjebiDialog(
    val mContext: Context,
    val callBack: (comment: String, amount: String) -> Unit
) : AppCompatDialogFragment() {
    private var eAmount: EditText? = null
    private var eComment: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(mContext)
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.xarjebi_dialog, null)
        builder.setView(view)
            .setTitle(getString(R.string.add_xarji))
            .setNegativeButton("cancel") { dialog, which ->
            }
            .setPositiveButton("ok") { dialog, which ->
                val comment = eComment!!.text.toString()
                val amount = eAmount!!.text.toString()
                callBack(comment, amount)
            }
        eAmount = view.findViewById(R.id.e_xarj_amount)
        eComment = view.findViewById(R.id.e_xarj_comment)
        return builder.create()
    }

}