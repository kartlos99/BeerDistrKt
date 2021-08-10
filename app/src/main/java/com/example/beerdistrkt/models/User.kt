package com.example.beerdistrkt.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.squareup.moshi.Json
import java.io.Serializable

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
    val comment: String
) {

    fun getIntID() = id.toInt()

    companion object {
        fun getBaseUser(): User {
            return User(
                "0", "ყველა", "ყველა",
                UserType.DISTRIBUTOR.value, "","","",""
            )
        }

        val EMPTY_USER = User("", "", "", UserType.DISTRIBUTOR.value, "", "", "", "")
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
    fun toUser(): User = User(
        userID.toString(),
        username,
        userDisplayName,
        "", "", "", "", ""
    )
}

enum class UserStatus(val value: String) {
    @Json(name = "1")
    ACTIVE("1"),

    @Json(name = "0")
    INACTIVE("0"),
}