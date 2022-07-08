package com.example.beerdistrkt.fragPages.addBeer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.beerdistrkt.R

class ChooseColorDialog(
    val callBack: (color: Int) -> Unit
) : AppCompatDialogFragment() {

    private var imgColor: ImageView? = null
    var posR = 0
    var posG: Int = 0
    var posB: Int = 0

    private val seekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            when (seekBar.id) {
                R.id.sbar_r -> posR = i
                R.id.sbar_g -> posG = i
                R.id.sbar_b -> posB = i
            }
            setColorToView()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    fun setColorToView() {
        imgColor!!.setBackgroundColor(Color.rgb(posR, posG, posB))
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewDlg: View = inflater.inflate(R.layout.choose_color, null)

        imgColor = viewDlg.findViewById(R.id.img_show_color)
        val seekBarR = viewDlg.findViewById<SeekBar>(R.id.sbar_r)
        val seekBarG = viewDlg.findViewById<SeekBar>(R.id.sbar_g)
        val seekBarB = viewDlg.findViewById<SeekBar>(R.id.sbar_b)

        seekBarR.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarG.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarB.setOnSeekBarChangeListener(seekBarChangeListener)

        val initialColor = Integer.valueOf(tag ?: "0")
        posR = Color.red(initialColor)
        posG = Color.green(initialColor)
        posB = Color.blue(initialColor)
        seekBarR.progress = posR
        seekBarG.progress = posG
        seekBarB.progress = posB
        setColorToView()

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setView(viewDlg)
            .setPositiveButton("ok") { dialog, id -> // FIRE ZE MISSILES!
                callBack.invoke(Color.rgb(posR, posG, posB))
            }
            .setNegativeButton("cancel") { dialog, id ->
                // User cancelled the dialog
            }
        return builder.create()
    }
}