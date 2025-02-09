package com.example.beerdistrkt.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.squareup.moshi.Json

@Deprecated("Don't use this")
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val id: String,
    val username: String,
    val name: String,
    val type: String,
    val tel: String,
    val adress: String,
    val maker: String,
    val comment: String,
    val userStatus: UserStatus
) {

    fun getIntID() = id.toInt()

    val isActive: Boolean
        get() = userStatus == UserStatus.ACTIVE

    companion object {
        fun getBaseUser(): User {
            return User(
                "0", "ყველა", "ყველა",
                UserType.DISTRIBUTOR.value, "", "", "", "", UserStatus.ACTIVE
            )
        }

        val EMPTY_USER =
            User("", "", "", UserType.DISTRIBUTOR.value, "", "", "", "", UserStatus.ACTIVE)
    }
}


data class MappedUser(
    val ID: Int,
    val userID: Int,
    val regionID: Int,
    val regionName: String,
    val username: String,
    val userDisplayName: String,
    val userStatus: UserStatus
) {
    val isActive: Boolean
        get() = userStatus == UserStatus.ACTIVE

    fun toUser(): User = User(
        userID.toString(),
        username,
        userDisplayName,
        "", "", "", "", "", userStatus
    )
}

enum class UserStatus(val value: String) {
    @Json(name = "0")
    DELETED("0"),

    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "2")
    INACTIVE("2"),
}