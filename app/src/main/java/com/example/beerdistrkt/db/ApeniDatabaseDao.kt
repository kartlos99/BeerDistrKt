package com.example.beerdistrkt.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.beerdistrkt.models.Obieqti

@Dao
interface ApeniDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObiecti(obieqti: Obieqti)

    @Query("DELETE FROM obieqts_table")
    fun clear()

    @Query("Select * from obieqts_table order by dasaxeleba")
    fun getAllObieqts(): LiveData<List<Obieqti>>
}