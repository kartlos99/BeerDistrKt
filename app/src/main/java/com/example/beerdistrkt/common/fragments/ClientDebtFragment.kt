package com.example.beerdistrkt.common.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.FragmentClientDebtBinding
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.DebtResponse
import com.example.beerdistrkt.utils.ApiResponseState

class ClientDebtFragment : BaseFragment<ClientDebtViewModel>() {

    override val viewModel: ClientDebtViewModel by lazy {
        getViewModel { ClientDebtViewModel() }
    }
    var clientID: Int? = null

    lateinit var binding: FragmentClientDebtBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientDebtBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clientID = arguments?.getInt(CLIENT_ID_KEY)
        clientID?.let {
            viewModel.getDebt(it)
        }
        viewModel.clientDebtLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> onDebtDataReceived(it.data)
                is ApiResponseState.Error -> showError()
                is ApiResponseState.Loading -> binding.progressIndicator.isVisible = it.showLoading
                else -> {}
            }
        }
    }

    private fun onDebtDataReceived(data: DebtResponse?) {
        if (data == null) {
            showError()
            return
        }
        binding.fragDebtAmount.text =
            boldDataSpan(getString(R.string.amount_is, data.getMoneyDebt()))
        val ssb = SpannableStringBuilder()
        data.barrels.forEach { emptyBarrel ->
            if (ssb.isNotEmpty()) ssb.append("\n")
            ssb.append("${emptyBarrel.canTypeName}: ${emptyBarrel.balance}")
        }
        binding.fragDebtBarrels.text = boldDataSpan(ssb.toString())
    }


    private fun boldDataSpan(text: String): Spannable {
        val spText = SpannableString(text)
        var currPos = text.indexOf(":") + 2
        while (currPos > 2) {
            val closeIndex =
                if (text.indexOf("\n", currPos) > 0) text.indexOf("\n", currPos) else text.length
            spText.setSpan(
                ForegroundColorSpan(Color.parseColor("#f95588")),
                currPos,
                closeIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spText.setSpan(
                StyleSpan(Typeface.BOLD),
                currPos,
                closeIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            if (text.subSequence(currPos, closeIndex).contains(DOT)) {
                spText.setSpan(
                    AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.sp13)),
                    text.indexOf(DOT, currPos),
                    text.indexOf(" ", currPos),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            currPos = text.indexOf(":", closeIndex) + 2
        }
        return spText
    }

    private fun showError() {
        binding.fragDebtBarrels.text = ""
        binding.fragDebtAmount.text = getString(R.string.debt_receive_error_text)
    }

    companion object {
        const val CLIENT_ID_KEY = "CLIENT_ID_KEY"
        const val DOT = "."

        fun getInstance(clientID: Int): ClientDebtFragment {
            return ClientDebtFragment().apply {
                arguments = bundleOf(CLIENT_ID_KEY to clientID)
            }
        }
    }
}