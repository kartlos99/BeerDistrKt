package com.example.beerdistrkt.fragPages.customer.data

import com.example.beerdistrkt.fragPages.customer.data.model.CustomerDTO
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBeerPrice
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.models.CustomerIdlInfo
import com.example.beerdistrkt.models.ObjToBeerPrice
import javax.inject.Inject

class CustomerMapper @Inject constructor() {

    fun toDomain(dto: CustomerDTO, idles: Map<Int, List<CustomerIdlInfo>>): Customer {

        return Customer(
            id = dto.id,
            name = dto.name.trim(),
            address = dto.address,
            tel = dto.tel,
            comment = dto.comment,
            identifyCode = dto.identifyCode,
            contactPerson = dto.contactPerson,
            status = dto.status,
            hasCheck = dto.chek == "1",
            warnInfo = idles[dto.id]?.firstOrNull(),
            group = dto.group,
            beerPrices = dto.beerPrices.map(::mapBeerPrice),
            bottlePrices = dto.bottlePrices
        )
    }

    private fun mapBeerPrice(priceDto: ObjToBeerPrice): ClientBeerPrice {

        return ClientBeerPrice(
            clientID = priceDto.clientID,
            beerID = priceDto.beerID,
            price = priceDto.price.toDouble(),
        )
    }

    fun toDto(customer: Customer): CustomerDTO {
        return CustomerDTO(
            id = customer.id,
            name = customer.name,
            group = customer.group,
            address = customer.address,
            tel = customer.tel,
            comment = customer.comment,
            identifyCode = customer.identifyCode,
            contactPerson = customer.contactPerson,
            status = customer.status,
            chek = if (customer.hasCheck) "1" else "0",
            beerPrices = customer.beerPrices.map(::mapBeerPriceToDto),
            bottlePrices = customer.bottlePrices
        )
    }

    private fun mapBeerPriceToDto(clientBeerPrice: ClientBeerPrice): ObjToBeerPrice =
        ObjToBeerPrice(
            clientID = clientBeerPrice.clientID,
            beerID = clientBeerPrice.beerID,
            price = clientBeerPrice.price.toFloat()
        )
}