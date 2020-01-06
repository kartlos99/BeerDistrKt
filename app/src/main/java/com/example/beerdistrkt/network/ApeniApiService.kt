package com.example.beerdistrkt.network

import android.content.Context
import android.util.Log
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.ObjToBeerPrice
import com.example.beerdistrkt.models.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


interface ApeniApiService {

    companion object {
        private var instance: ApeniApiService? = null

        const val BASE_URL = "https://apeni.ge/andr_app_links/"

        fun initialize(context: Context){
            if (instance == null){
                instance = create(context)
            }
        }

        fun get(): ApeniApiService {
            return instance!!
        }

        private fun create(context: Context?): ApeniApiService{

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApeniApiService::class.java)
        }
    }


    @GET("get_users.php")
    fun getProperties(): Call<List<User>>

    @GET("get_obieqts.php")
    fun getObieqts(): Call<List<Obieqti>>

    @GET("get_fasebi.php")
    fun getPrices(): Call<List<ObjToBeerPrice>>
}
