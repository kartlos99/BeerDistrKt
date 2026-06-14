package com.example.beerdistrkt.fragPages.barrel.presentation.mapper

import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelMatchStatus
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsInfo
import com.example.beerdistrkt.fragPages.barrel.domain.model.EmptyBarrelsIo
import com.example.beerdistrkt.fragPages.barrel.presentation.model.EmptyBarrelUiModel
import javax.inject.Inject

class EmptyBarrelsUiMapper @Inject constructor() {

    fun mapToUI(barrelsInfo: EmptyBarrelsInfo): EmptyBarrelUiModel {

        return EmptyBarrelUiModel(
            customerName = barrelsInfo.client.name,
            orderStatus = barrelsInfo.orderStatus,
            barrelOutputStatus = getOutputStatus(barrelsInfo.barrels),
            barrels = barrelsInfo.barrels
        )
    }

    private fun getOutputStatus(barrels: List<EmptyBarrelsIo>): EmptyBarrelMatchStatus {
        val isLessOutput = barrels.any {
            it.outputCount < it.inputCount
        }
        val isExtraOutput = barrels.any {
            it.outputCount > it.inputCount
        }
        return when {
            isLessOutput -> EmptyBarrelMatchStatus.LESS
            isExtraOutput -> EmptyBarrelMatchStatus.MORE
            else -> EmptyBarrelMatchStatus.MATCH
        }
    }
}