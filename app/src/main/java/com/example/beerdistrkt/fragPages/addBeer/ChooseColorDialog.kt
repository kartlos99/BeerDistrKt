package com.example.beerdistrkt.fragPages.addBeer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ChooseColorDialogBinding

class ChooseColorDialog : DialogFragment() {

    private var _binding: ChooseColorDialogBinding? = null
    private val binding: ChooseColorDialogBinding
        get() = _binding!!

    private var posR: Int = 0
    private var posG: Int = 0
    private var posB: Int = 0

    private val initialColor: Int? by lazy {
        requireArguments().getInt(SET_COLOR_PARAM)
    }

    private val seekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
            when (seekBar.id) {
                R.id.seekbar_r -> posR = i
                R.id.seekbar_g -> posG = i
                R.id.seekbar_b -> posB = i
            }
            setColorToView()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    fun setColorToView() {
        binding.imgShowColor.setBackgroundColor(Color.rgb(posR, posG, posB))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ColorChooserStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChooseColorDialogBinding.inflate(inflater, container, false)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        seekbarR.setOnSeekBarChangeListener(seekBarChangeListener)
        seekbarG.setOnSeekBarChangeListener(seekBarChangeListener)
        seekbarB.setOnSeekBarChangeListener(seekBarChangeListener)

        initialColor?.let { color ->
            posR = Color.red(color)
            posG = Color.green(color)
            posB = Color.blue(color)
            seekbarR.progress = posR
            seekbarG.progress = posG
            seekbarB.progress = posB
            setColorToView()
        }

        positiveBtn.setOnClickListener {
            setFragmentResult(
                COLOR_SELECTOR_REQUEST_KEY, bundleOf(
                    SELECTED_COLOR_KEY to Color.rgb(posR, posG, posB),
                )
            )
            dismiss()
        }
        dismissBtn.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ChooseColorDialog"
        const val COLOR_SELECTOR_REQUEST_KEY = "COLOR_SELECTOR_REQUEST_KEY"
        const val SELECTED_COLOR_KEY = "SELECTED_COLOR_KEY"
        const val SET_COLOR_PARAM = "SET_COLOR_PARAM"

        fun getInstance(initialColor: Int?) = ChooseColorDialog().apply {
            arguments = bundleOf(
                SET_COLOR_PARAM to initialColor
            )
        }
    }
}