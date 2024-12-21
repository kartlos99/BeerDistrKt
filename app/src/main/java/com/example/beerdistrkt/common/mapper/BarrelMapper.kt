package com.example.beerdistrkt.common.mapper

import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.common.model.BarrelDto
import javax.inject.Inject


class BarrelMapper @Inject constructor() {

    fun toDomain(dto: BarrelDto): Barrel = Barrel(
        id = dto.id,
        name = dto.name,
        volume = dto.volume,
        status = dto.status,
        sortValue = dto.sortValue,
        image = dto.image
    )

}