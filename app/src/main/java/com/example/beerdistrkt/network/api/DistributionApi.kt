package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.network.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
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


}