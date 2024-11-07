package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.fragPages.beer.data.model.BeerDto
import com.example.beerdistrkt.fragPages.beer.data.model.BeerOrderingUpdateDto
import com.example.beerdistrkt.fragPages.expense.data.model.DeleteExpenseCategoryRequestDto
import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseCategoryDto
import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseDto
import com.example.beerdistrkt.fragPages.realisationtotal.data.model.RealizationDayDto
import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.network.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface DistributionApi {

    companion object {
        private var instance: DistributionApi? = null

        fun getApi(): DistributionApi {
            return instance ?: create().also {
                instance = it
            }
        }

        private fun create(): DistributionApi {

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BuildConfig.MOBILE_API_URL)
                .build()

            return retrofit.create(DistributionApi::class.java)
        }
    }

    @GET("storeHouse/getIoListPaged.php")
    suspend fun getStoreHouseIoPagedList(
        @Query("pageIndex") pageIndex: Int,
        @Query("groupID") groupID: String = ""
    ): List<StorehouseIoDto>

    @GET("listing/expenseCategories.php")
    suspend fun getExpenseCategories(): List<ExpenseCategoryDto>

    @POST("expense/putCategory.php")
    suspend fun putExpenseCategory(
        @Body data: ExpenseCategoryDto
    ): List<ExpenseCategoryDto>

    @POST("expense/deleteCategory.php")
    suspend fun deleteExpenseCategory(
        @Body data: DeleteExpenseCategoryRequestDto
    ): List<ExpenseCategoryDto>

    @POST("expense/putExpense.php")
    suspend fun putExpense(
        @Body data: ExpenseDto
    ): Any

    @GET("realization/getDayTotal.php")
    suspend fun getRealizationDayInfo(
        @Query("date") date: String,
        @Query("distributorId") distributorId: Int
    ): RealizationDayDto

    /* BEER */
    @POST("beer/updateSortValue.php")
    suspend fun updateBeerSortValue(@Body data: BeerOrderingUpdateDto): List<BeerDto>

    @POST("beer/delete.php")
    suspend fun deleteBeer(@Body beerId: Int): List<BeerDto>

    @POST("beer/add.php")
    suspend fun putBeer(@Body beer: BeerDto): List<BeerDto>

    @GET("listing/beers.php")
    suspend fun getBeers(): List<BeerDto>
}