package com.example.beerdistrkt.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.ObjToBeerPrice

@Dao
interface ApeniDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObiecti(obieqti: Obieqti)

    @Query("DELETE FROM obieqts_table")
    fun clearObiectsTable()

    @Query("Select * from obieqts_table order by dasaxeleba")
    fun getAllObieqts(): LiveData<List<Obieqti>>

    @Transaction
    @Query("SELECT * FROM obieqts_table WHERE id = :obiectisID")
    fun getObiectsWithPrices(obiectisID: Int): ObiectWithPrices


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBeerPrice(objToBeerPrice: ObjToBeerPrice)

    @Query("DELETE FROM prices_table")
    fun clearPricesTable()
}