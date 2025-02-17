package com.example.beerdistrkt.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.beerdistrkt.common.model.BarrelDto

@Dao
interface ApeniDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBarrels(barrels: List<BarrelDto>)

    @Query("DELETE FROM $BARREL_TB_NAME")
    fun clearBarrelsTable()

    @Query("Select * from $BARREL_TB_NAME order by sortValue")
    fun getBarrels(): LiveData<List<BarrelDto>>

    companion object {
        const val BARREL_TB_NAME = "barrels"
    }
}