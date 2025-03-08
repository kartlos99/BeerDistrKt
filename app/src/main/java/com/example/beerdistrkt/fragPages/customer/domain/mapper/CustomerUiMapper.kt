package com.example.beerdistrkt.fragPages.customer.domain.mapper

import com.example.beerdistrkt.fragPages.customer.presentation.model.PriceEditModel
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBeerPrice
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerUiModel
import com.example.beerdistrkt.orZero
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
                    ?: defaultPrice

                PriceEditModel(
                    id = beer.id,
                    displayName = beer.name,
                    defaultPrice = defaultPrice.toString(),
                    price = price.toString(),
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
                    ?: defaultPrice

                PriceEditModel(
                    id = bottle.id,
                    displayName = bottle.name,
                    defaultPrice = defaultPrice.toString(),
                    price = price.toString(),
                )
            }
    }

    suspend fun mapToUi(customer: Customer?): CustomerUiModel {
        return if (customer == null)
            CustomerUiModel()
        else
            CustomerUiModel(
                id = customer.id,
                name = customer.name,
                address = customer.address.orEmpty(),
                tel = customer.tel.orEmpty(),
                comment = customer.comment.orEmpty(),
                identifyCode = customer.identifyCode.orEmpty(),
                contactPerson = customer.contactPerson.orEmpty(),
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
            name = uiModel.name,
            address = uiModel.address,
            tel = uiModel.tel,
            comment = uiModel.comment,
            identifyCode = uiModel.identifyCode,
            contactPerson = uiModel.contactPerson,
            status = uiModel.status,
            hasCheck = uiModel.hasCheck,
            warnInfo = uiModel.warnInfo,
            group = uiModel.group,
            beerPrices = uiModel.beerPrices.map {
                ClientBeerPrice(
                    clientID = uiModel.id.orZero(),
                    beerID = it.id,
                    price = it.price.toDouble()
                )
            },
            bottlePrices = uiModel.bottlePrices.map {
                ClientBottlePrice(
                    clientID = uiModel.id.orZero(),
                    bottleID = it.id,
                    price = it.price.toDouble()
                )
            }
        )
    }
}
