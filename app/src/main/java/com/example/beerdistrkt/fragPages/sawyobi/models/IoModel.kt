package com.example.beerdistrkt.fragPages.sawyobi.models

data class IoModel(
    val ID: Int,
    val ioDate: String,
    val distributorID: Int,
    val beerID: Int,
    val barrelID: Int,
    val count: Int,
    val chek: Int,
    val comment: String?,
    val modifyDate: String,
    val modifyUserID: Int
) {
}