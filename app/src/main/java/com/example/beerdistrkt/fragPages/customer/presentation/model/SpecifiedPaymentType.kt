package com.example.beerdistrkt.fragPages.customer.presentation.model

import androidx.annotation.StringRes
import com.example.beerdistrkt.R

enum class SpecifiedPaymentType(
    @StringRes val textRes: Int
) {
    NONE(R.string.none),
    CASH(R.string.payment_type_cash),
    TRANSFER(R.string.payment_type_bank);
}