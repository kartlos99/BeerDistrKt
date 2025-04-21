package com.example.beerdistrkt.fragPages.user.data

import com.example.beerdistrkt.fragPages.user.data.model.UserApiModel
import com.example.beerdistrkt.fragPages.user.data.model.WorkRegionDto
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import javax.inject.Inject

class UserMapper @Inject constructor() {

    fun toDomain(userApiModel: UserApiModel): List<User> {
        return userApiModel.users.map { userDto ->
            User(
                id = userDto.id,
                username = userDto.username,
                name = userDto.name,
                type = userDto.type,
                tel = userDto.tel,
                address = userDto.address,
                maker = userDto.maker,
                comment = userDto.comment,
                userStatus = userDto.userStatus,
                regions = userDto.regions.mapNotNull { regionId ->
                    userApiModel.regions.find { it.id == regionId }?.let(::mapRegion)
                }
            )
        }
    }

    fun mapRegion(regionDto: WorkRegionDto) = WorkRegion(
        id = regionDto.id,
        code = regionDto.code,
        name = regionDto.name,
        status = regionDto.status,
        hasOwnStorage = regionDto.ownStorage == 1
    )
}