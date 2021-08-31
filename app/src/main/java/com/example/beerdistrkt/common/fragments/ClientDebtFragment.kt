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
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import kotlinx.android.synthetic.main.fragment_client_debt.*

class ClientDebtFragment : Fragment(R.layout.fragment_client_debt) {

    var clientID: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clientID = arguments?.getInt(CLIENT_ID_KEY)
    }

    override fun onResume() {
        super.onResume()
        clientID?.let {
            getDebt(it)
        }
    }

    private fun getDebt(clientID: Int) {
        ApeniApiService.getInstance().getDebt(clientID).sendRequest(
            successWithData = {
                fragDebtAmount?.let { tv ->
                    tv.text = boldDataSpan(getString(R.string.amount_is, it.getMoneyDebt()))
                }
                val ssb = SpannableStringBuilder()
                it.barrels.forEach { emptyBarrel ->
                    if (ssb.isNotEmpty()) ssb.append("\n")
                    ssb.append("${emptyBarrel.canTypeName}: ${emptyBarrel.balance}")
                }
                fragDebtBarrels?.let { tv ->
                    tv.text = boldDataSpan(ssb.toString())
                }
            },
            failure = {
                showError()
            },
            finally = {
                if (!it)
                    showError()
            },
            onConnectionFailure = {}
        )
    }


    private fun boldDataSpan(text: String): Spannable {
        val spText = SpannableString(text)
        var currPos = text.indexOf(":") + 2
        while (currPos > 2) {
            val closeIndex = if (text.indexOf("\n", currPos) > 0) text.indexOf("\n", currPos) else text.length
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
        fragDebtBarrels.text = ""
        fragDebtAmount.text = getString(R.string.debt_receive_error_text)
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