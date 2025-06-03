package com.example.beerdistrkt.fragPages.customer.domain.mapper

import com.example.beerdistrkt.asDouble
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBeerPrice
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerUiModel
import com.example.beerdistrkt.fragPages.customer.presentation.model.PriceEditModel
import com.example.beerdistrkt.fragPages.customer.presentation.model.SpecifiedPaymentType
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.toFormatedString
import javax.inject.Inject

class CustomerUiMapper @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottlesUseCase: GetBottlesUseCase,
) {

    private suspend fun getBeerPrices(beerPrices: List<ClientBeerPrice>?): List<PriceEditModel> {
        return getBeerUseCase()
            .filter { it.isActive }
            .map { beer ->
                val defaultPrice = beer.price ?: 0.0
                // price for the customer
                val price = beerPrices?.firstOrNull {
                    it.beerID == beer.id
                }?.price
                    ?: .0

                PriceEditModel(
                    id = beer.id,
                    displayName = beer.name,
                    defaultPrice = defaultPrice.toFormatedString(),
                    price = price.toFormatedString(),
                )
            }
    }

    private suspend fun getBottlePrices(bottlePrices: List<ClientBottlePrice>?): List<PriceEditModel> {
        return getBottlesUseCase()
            .filter { it.isActive }
            .map { bottle ->
                val defaultPrice = bottle.price
                val price = bottlePrices?.firstOrNull {
                    it.bottleID == bottle.id
                }?.price
                    ?: .0

                PriceEditModel(
                    id = bottle.id,
                    displayName = bottle.name,
                    defaultPrice = defaultPrice.toFormatedString(),
                    price = price.toFormatedString(),
                )
            }
    }

    suspend fun mapToUi(customer: Customer?): CustomerUiModel {
        return if (customer == null)
            CustomerUiModel(
                beerPrices = getBeerPrices(null),
                bottlePrices = getBottlePrices(null)
            )
        else
            CustomerUiModel(
                id = customer.id,
                name = customer.name,
                address = customer.address.orEmpty(),
                tel = customer.tel.orEmpty(),
                comment = customer.comment.orEmpty(),
                identifyCode = customer.identifyCode.orEmpty(),
                contactPerson = customer.contactPerson.orEmpty(),
                location = customer.location.orEmpty(),
                specifiedPaymentType = when (customer.paymentType) {
                    PaymentType.Cash -> SpecifiedPaymentType.CASH
                    PaymentType.Transfer -> SpecifiedPaymentType.TRANSFER
                    null -> SpecifiedPaymentType.NONE
                },
                status = customer.status,
                hasCheck = customer.hasCheck,
                warnInfo = customer.warnInfo,
                group = customer.group,
                beerPrices = getBeerPrices(customer.beerPrices),
                bottlePrices = getBottlePrices(customer.bottlePrices)
            )
    }

    fun toDomain(uiModel: CustomerUiModel): Customer {
        return Customer(
            id = uiModel.id,
            name = uiModel.name.trim(),
            address = uiModel.address.trim(),
            tel = uiModel.tel.trim(),
            comment = uiModel.comment.trim(),
            identifyCode = uiModel.identifyCode.trim(),
            contactPerson = uiModel.contactPerson.trim(),
            location = uiModel.location.trim(),
            paymentType = when(uiModel.specifiedPaymentType) {
                SpecifiedPaymentType.NONE -> null
                SpecifiedPaymentType.CASH -> PaymentType.Cash
                SpecifiedPaymentType.TRANSFER -> PaymentType.Transfer
            },
            status = uiModel.status,
            hasCheck = uiModel.hasCheck,
            warnInfo = uiModel.warnInfo,
            group = uiModel.group,
            beerPrices = uiModel.beerPrices.map {
                ClientBeerPrice(
                    clientID = uiModel.id.orZero(),
                    beerID = it.id,
                    price = it.price.asDouble()
                )
            },
            bottlePrices = uiModel.bottlePrices.map {
                ClientBottlePrice(
                    clientID = uiModel.id.orZero(),
                    bottleID = it.id,
                    price = it.price.asDouble()
                )
            }
        )
    }
}
