package com.example.beerdistrkt.fragPages.customer.presentation

import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.domain.model.ValidationResult
import com.example.beerdistrkt.fragPages.customer.domain.mapper.CustomerUiMapper
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerUiModel
import com.example.beerdistrkt.fragPages.customer.presentation.model.PriceEditModel
import com.example.beerdistrkt.utils.DOUBLE_PRECISION
import javax.inject.Inject


class CustomerValidator @Inject constructor(
    private val customerUiMapper: CustomerUiMapper,
) {

    fun getCustomerStatus(customerUiModel: CustomerUiModel): ValidationResult<Customer> {

        return when {
            isNameValid(customerUiModel.name) == null -> ValidationResult.NotValid(R.string.incorrect_customer_name_warning)
            !isPricesValid(customerUiModel.beerPrices) -> ValidationResult.NotValid(R.string.incorrect_beer_price_warning)
            !isPricesValid(customerUiModel.bottlePrices) -> ValidationResult.NotValid(R.string.incorrect_bottle_price_warning)

            else -> mapToCustomer(customerUiModel)
        }
    }

    private fun isPricesValid(prices: List<PriceEditModel>): Boolean {
        return prices.all {
            try {
                it.price.toDouble() in DOUBLE_PRECISION..1000.0
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun mapToCustomer(customerUiModel: CustomerUiModel): ValidationResult<Customer> {
        return try {
            ValidationResult.IsValid(
                customerUiMapper.toDomain(customerUiModel)
            )
        } catch (e: Exception) {
            ValidationResult.NotValid(R.string.something_is_wrong)
        }
    }

    private fun isNameValid(name: String): String? {
        return if (name.trim().length in 3..100) {
            name.trim()
        } else {
            null
        }
    }

}
