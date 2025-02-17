package com.example.beerdistrkt.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.beerdistrkt.common.model.BarrelDto

@Database(
    entities = [
        BarrelDto::class
    ],
    version = 20,
    exportSchema = false
)
abstract class ApeniDataBase : RoomDatabase() {

    abstract val apeniDataBaseDao: ApeniDatabaseDao

}