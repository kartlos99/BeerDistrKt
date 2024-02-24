package com.example.beerdistrkt.fragPages.sawyobi.models

import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseIO
import com.example.beerdistrkt.utils.DiffItem

data class StorehouseIoPm(
    val groupID: String,
    val ioDate: String,
    val distributorID: Int,
    val check: Int,
    val comment: String?,
    val barrelItems: List<SimpleBeerRowModel>? = null,
    val bottleItems: List<SimpleBottleRowModel>? = null
) : DiffItem {

    val ioCount: Int
        get() = (barrelItems?.size ?: 0) + (bottleItems?.size ?: 0)

    override val key: String
        get() = groupID

    companion object {

        fun fromDomainIo(ioItem: StorehouseIO): StorehouseIoPm {

            val barrelItemsIo = mutableListOf<SimpleBeerRowModel>()

            ioItem.barrelInput
                ?.groupBy { it.beer.id }
                ?.values
                ?.forEach { singleBeerList ->
                    val splitedByBarrelMap = singleBeerList.groupBy { it.barrelID }

                    val countByBarrelMap = splitedByBarrelMap.mapValues { input ->
                        input.value.sumOf { it.count }
                    }

                    val title = singleBeerList[0].beer.dasaxeleba ?: "*"
                    val icon = R.drawable.ic_beer_input_24
                    val iconColor = R.color.green_09

                    barrelItemsIo.add(
                        SimpleBeerRowModel(
                            title,
                            countByBarrelMap,
                            icon,
                            iconColor,
                            singleBeerList[0].beer.displayColor
                        )
                    )
                }

            ioItem.barrelOutput?.let { singleBeerList ->
                val splitedByBarrelMap = singleBeerList.groupBy { it.barrelID }

                val countByBarrelMap = splitedByBarrelMap.mapValues { output ->
                    output.value.sumOf { it.count }
                }

                val title = "- ცარიელი -"
                val icon = R.drawable.ic_barrel_output_24
                val iconColor = R.color.orange_08

                barrelItemsIo.add(
                    SimpleBeerRowModel(
                        title,
                        countByBarrelMap,
                        icon,
                        iconColor
                    )
                )
            }

            return StorehouseIoPm(
                ioItem.groupID,
                ioItem.ioDate,
                ioItem.distributorID,
                ioItem.check,
                ioItem.comment,
                barrelItems = barrelItemsIo,
                bottleItems = ioItem.bottleInput?.map {
                    SimpleBottleRowModel(
                        it.bottle.name,
                        it.count,
                        it.bottle.imageLink
                    )
                }
            )
        }
    }
}