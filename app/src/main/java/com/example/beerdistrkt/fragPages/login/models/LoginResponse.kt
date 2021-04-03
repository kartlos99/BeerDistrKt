package com.example.beerdistrkt.fragPages.login.models

import com.squareup.moshi.Json

data class LoginResponse(
    val id: Int,
    val username: String,
    val name: String,
    val type: UserType,
    val permissions: List<Permission>,
    val token: String
) {
}


enum class UserType(val value: String) {
    @Json(name = "9")
    ADMIN("9"),

    @Json(name = "10")
    MANAGER("10"),

    @Json(name = "11")
    DISTRIBUTOR("11")
}

enum class Permission(val code: String) {
    @Json(name = "1")
    AddEditBeer("1"),
    @Json(name = "2")
    AddEditUser("2"),
    @Json(name = "3")
    AddEditClient("3"),
    @Json(name = "4")
    DeleteClient("4"),
    @Json(name = "5")
    AddEditStoreHouse("5"),
    @Json(name = "6")
    EditOrder("6"),
    @Json(name = "7")
    EditOldOrder("7"),
    @Json(name = "8")
    EditSale("8"),
    @Json(name = "9")
    EditOldSale("9"),
    @Json(name = "10")
    SeeOthersRealization("10"),
    @Json(name = "11")
    SeeOldRealization("11"),
    @Json(name = "12")
    DeleteExpense("12"),
    @Json(name = "13")
    AddExpenseInPast("13")
}