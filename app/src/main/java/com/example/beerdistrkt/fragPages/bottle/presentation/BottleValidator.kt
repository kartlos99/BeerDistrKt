package com.example.beerdistrkt.fragPages.bottle.presentation

import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.domain.model.ValidationResult
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.presentation.model.BottleUiModel

class BottleValidator {

    fun getBottleStatus(bottleUiModel: BottleUiModel): ValidationResult<Bottle> {

        return when {
            isNameValid(bottleUiModel.name) == null -> ValidationResult.NotValid(R.string.incorrect_bottle_name)
            isVolumeValid(bottleUiModel.volume) == null -> ValidationResult.NotValid(R.string.incorrect_volume)
            isBeerValid(bottleUiModel.beer) == null -> ValidationResult.NotValid(R.string.incorrect_beer)
            isPriceValid(bottleUiModel.price) == null -> ValidationResult.NotValid(R.string.incorrect_price)
            else -> mapToBottle(bottleUiModel)
        }
    }

    private fun mapToBottle(bottleUiModel: BottleUiModel): ValidationResult<Bottle> {
        return try {
            ValidationResult.IsValid(
                Bottle(
                    id = bottleUiModel.id,
                    name = bottleUiModel.name,
                    volume = isVolumeValid(bottleUiModel.volume)!!,
                    actualVolume = isVolumeValid(bottleUiModel.volume)!!,
                    beer = bottleUiModel.beer!!,
                    price = isPriceValid(bottleUiModel.price)!!,
                    status = bottleUiModel.status,
                    sortValue = bottleUiModel.sortValue,
                    imageFileName = bottleUiModel.imageFileName
                )
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

    private fun isVolumeValid(volumeStr: String): Double? {
        return try {
            volumeStr.toDouble().takeIf { it > 0.001 }
        } catch (e: Exception) {
            null
        }
    }

    private fun isBeerValid(beer: Beer?): Beer? {
        return beer.takeIf {
            (it?.id ?: 0) > 0
        }
    }

    private fun isPriceValid(priceStr: String): Double? {
        return try {
            priceStr.toDouble().takeIf { it >= .0 }
        } catch (e: Exception) {
            null
        }
    }
}
