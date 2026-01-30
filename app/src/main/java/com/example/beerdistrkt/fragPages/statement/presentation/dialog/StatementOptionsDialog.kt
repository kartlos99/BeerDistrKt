package com.example.beerdistrkt.fragPages.statement.presentation.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.StatementOptionsDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.parcelize.Parcelize

class StatementOptionsDialog : BottomSheetDialogFragment() {

    private val binding by viewBinding(StatementOptionsDialogBinding::bind)

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
    }

    private fun closeWithResult(action: StatementOption) {
        setFragmentResult(OPTIONS_REQUEST_KEY, bundleOf(ACTION_KEY to action))
        dismiss()
    }

    companion object {
        const val OPTIONS_REQUEST_KEY = "OPTIONS_REQUEST_KEY"
        const val ACTION_KEY = "ACTION_KEY"
        const val TAG = "StatementOptionsDialogTag"
    }
}

@Parcelize
enum class StatementOption : Parcelable {
    HISTORY,
    EDIT,
    DELETE,
}