package com.example.beerdistrkt.fragPages.sawyobi.models

data class StoreHouseResponse(
    val empty: List<EmptyBarrelModel>?,
    val full: List<FullBarrelModel>
) {
    data class EmptyBarrelModel(
        val barrelID: Int,
        val outputEmptyFromStoreCount: Int,
        val inputEmptyToStore: Int
    )

    data class FullBarrelModel(
        val beerID: Int,
        val barrelID: Int,
        val inputToStore: Int,
        val saleCount: Int
    )
}

data class GlobalStorageModel(
    val id: Int,
    val barrelName: String,
    val initialAmount: Int,
    val globalIncome: Int,
    val globalOutput: Int,
) {
    fun getBalance() = initialAmount + globalIncome - globalOutput
}