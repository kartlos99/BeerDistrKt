package com.example.beerdistrkt.fragPages.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.showSoftKeyboard
import com.example.beerdistrkt.waitFor

class EditValueDialog(
    val title: String,
    val defaultValue: String = "",
    val callBack: (newValue: String) -> Unit
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val editText: EditText = LayoutInflater.from(requireContext())
            .inflate(R.layout.edit_value_dialog_layout, null) as EditText
        editText.hint = defaultValue

        val builder = AlertDialog.Builder(requireContext())
        builder
            .setView(editText)
            .setTitle(title)
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .setPositiveButton(R.string.common_save) { _, _ ->
                callBack.invoke(editText.text.toString())
            }
        200 waitFor {
            requireContext().showSoftKeyboard(editText)
        }
        return builder.create()
    }

    companion object {
        const val TAG = "EditValueDialog"
    }
}