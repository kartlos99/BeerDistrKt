package com.example.beerdistrkt.models

import androidx.room.Entity
import androidx.room.PrimaryKey
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
)

class Useri(var id: Int, var username: String) : Serializable {
    var type = 0
    var name: String? = null
    var pass: String? = null
    var tel: String? = null
    var adress: String? = null
    var comment: String? = null
    var maker: String? = null

    override fun toString(): String { //        return "Useri{" +
//                "id=" + id +
//                ", type=" + type +
//                ", maker=" + maker +
//                ", username='" + username + '\'' +
//                ", name='" + name + '\'' +
//                ", tel='" + tel + '\'' +
//                ", adress='" + adress + '\'' +
//                ", comment='" + comment + '\'' +
//                '}';
        return name ?: "noName"
    }

}