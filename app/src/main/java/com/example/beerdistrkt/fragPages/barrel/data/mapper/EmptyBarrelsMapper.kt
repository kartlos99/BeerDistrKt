package com.example.beerdistrkt.fragPages.barrel.data.mapper

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.barrel.data.model.EmptyBarrelsInfoDto
import com.example.beerdistrkt.fragPages.barrel.data.model.EmptyBarrelsIoDto
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsInfo
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsIo
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomerUseCase
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.GetBarrelsUseCase
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUserUseCase
import javax.inject.Inject

class EmptyBarrelsMapper @Inject constructor(
    private val getCustomerUseCase: GetCustomerUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val barrelsUseCase: GetBarrelsUseCase,
) {

    suspend fun map(dto: EmptyBarrelsInfoDto): EmptyBarrelsInfo? {
        val customer = getCustomerUseCase(dto.clientID) ?: return null
        val distributor = getUserUseCase(dto.distributorID.toString()) ?: return null
        val allBarrels = barrelsUseCase()

        return EmptyBarrelsInfo(
            orderID = dto.orderID,
            client = customer,
            distributor = distributor,
            orderStatus = dto.orderStatus,
            orderDate = dto.orderDate,
            barrels = mapBarrelsIo(dto.barrels, allBarrels)
        )
    }

    private fun mapBarrelsIo(
        ioListDto: List<EmptyBarrelsIoDto>,
        allBarrels: List<Barrel>,
    ): List<EmptyBarrelsIo> {

        return ioListDto.mapNotNull { ioDto ->
            allBarrels.firstOrNull {
                it.id == ioDto.barrelId
            }?.let { barrel ->
                EmptyBarrelsIo(
                    barrel = barrel,
                    inputCount = ioDto.inputCount,
                    outputCount = ioDto.outputCount,
                )
            }
        }
    }
}