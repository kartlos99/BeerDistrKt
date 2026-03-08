package com.example.beerdistrkt.fragPages.statement.presentation.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.StatementOptionsDialogBinding
import com.example.beerdistrkt.utils.K_OUT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.parcelize.Parcelize

class StatementOptionsDialog : BottomSheetDialogFragment() {

    private val binding by viewBinding(StatementOptionsDialogBinding::bind)

    private val source by lazy {
        requireArguments().getString(SOURCE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.statement_options_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() = with(binding) {
        optionHistory.setOnClickListener { closeWithResult(StatementOption.HISTORY) }
        optionEdit.setOnClickListener { closeWithResult(StatementOption.EDIT) }
        optionDelete.setOnClickListener { closeWithResult(StatementOption.DELETE) }

        optionHistory.isVisible = source != K_OUT
    }

    private fun closeWithResult(action: StatementOption) {
        setFragmentResult(OPTIONS_REQUEST_KEY, bundleOf(ACTION_KEY to action))
        dismiss()
    }

    companion object {
        const val OPTIONS_REQUEST_KEY = "OPTIONS_REQUEST_KEY"
        const val ACTION_KEY = "ACTION_KEY"
        const val TAG = "StatementOptionsDialogTag"
        private const val SOURCE = "source"

        fun newInstance(source: String): StatementOptionsDialog {
            return StatementOptionsDialog().apply {
                arguments = bundleOf(SOURCE to source)
            }
        }
    }
}

@Parcelize
enum class StatementOption : Parcelable {
    HISTORY,
    EDIT,
    DELETE,
}