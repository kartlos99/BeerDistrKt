package com.example.beerdistrkt.common.domain.model

sealed class ValidationResult<out T : Any> {
    data class IsValid<T : Any>(val item: T) : ValidationResult<T>()
    data class NotValid(val message: Int) : ValidationResult<Nothing>()
}