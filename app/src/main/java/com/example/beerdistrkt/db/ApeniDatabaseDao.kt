package com.example.beerdistrkt.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.beerdistrkt.models.*

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

    // users operation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("DELETE FROM user_table")
    fun clearUserTable()

    @Query("DELETE FROM user_table WHERE id = :userID")
    fun deleteUser(userID: String)

    @Query("Select * from user_table order by username")
    fun getUsers(): LiveData<List<User>>

    // beer operation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBeer(beerModel: BeerModel)

    @Query("DELETE FROM beer_table")
    fun clearBeerTable()

    @Query("Select * from beer_table order by sortValue")
    fun getBeerList(): LiveData<List<BeerModel>>

    // cans (kasrebi)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCan(canModel: CanModel)

    @Query("DELETE FROM can_table")
    fun clearCansTable()

    @Query("Select * from can_table order by sortValue")
    fun getCansList(): LiveData<List<CanModel>>

}