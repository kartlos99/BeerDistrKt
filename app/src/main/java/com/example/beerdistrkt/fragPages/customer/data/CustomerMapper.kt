package com.example.beerdistrkt.fragPages.customer.data

import com.example.beerdistrkt.fragPages.customer.data.model.CustomerDTO
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBeerPrice
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.models.ObjToBeerPrice
import javax.inject.Inject

class CustomerMapper @Inject constructor() {

    fun toDomain(dto: CustomerDTO): Customer {

        return Customer(
            id = dto.id,
            name = dto.dasaxeleba,
            address = dto.address,
            tel = dto.tel,
            comment = dto.comment,
            identifyCode = dto.sk,
            contactPerson = dto.sakpiri,
            chek = dto.chek,
            warnInfo = null,
            group = dto.group,
            beerPrices = dto.prices.map(::mapBeerPrice),
            bottlePrices = dto.bottlePrices
        )
    }

    private fun mapBeerPrice(priceDto: ObjToBeerPrice): ClientBeerPrice {

        return ClientBeerPrice(
            clientID = priceDto.objID,
            beerID = priceDto.beerID,
            price = priceDto.fasi.toDouble(),
        )
    }
}