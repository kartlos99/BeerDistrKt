package com.example.beerdistrkt.fragPages.login.domain.model

import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.squareup.moshi.Json

data class LoginData(
    val id: Int,
    val username: String,
    val name: String,
    val type: UserType,
    val permissions: List<Permission>,
    val regions: List<WorkRegion>,
    val token: String
)

data class WorkRegion(
    val regionID: String,
    val name: String,
    val ownStorage: Int
) {
    fun hasOwnStorage(): Boolean = ownStorage == 1
}

data class AttachedRegion(
    val ID: String,
    val name: String,
    val ownStorage: Int,
    var attached: Int
) {
    var isAttached: Boolean = false
        get() {
            return attached > 0
        }
        set(value) {
            attached = if (value) 1 else 0
            field = value
        }

//    fun toWorkRegion(): WorkRegion {
//        return WorkRegion(ID, name, ownStorage)
//    }
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
    AddExpenseInPast("13"),

    @Json(name = "14")
    ManageRegion("14")
}