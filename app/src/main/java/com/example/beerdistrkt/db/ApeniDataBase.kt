package com.example.beerdistrkt.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.ObjToBeerPrice

@Database(entities = [Obieqti::class, ObjToBeerPrice::class], version = 3, exportSchema = false)
abstract class ApeniDataBase : RoomDatabase() {

    abstract val apeniDataBaseDao: ApeniDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: ApeniDataBase? = null

        fun initialize(context: Context){
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ApeniDataBase::class.java,
                        "apeni_local_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
            }
        }

        fun getInstance(): ApeniDataBase {
            return INSTANCE!!
        }
    }
}