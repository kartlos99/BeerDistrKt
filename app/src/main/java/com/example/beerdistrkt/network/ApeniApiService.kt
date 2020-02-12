package com.example.beerdistrkt.network

import android.content.Context
import android.util.Log
import com.example.beerdistrkt.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.RequestBody
//import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.*


interface ApeniApiService {

    companion object {
        private var instance: ApeniApiService? = null

        const val BASE_URL = "https://apeni.ge/tbilisi/mobile/"

        fun initialize(context: Context) {
            if (instance == null) {
                instance = create(context)
            }
        }

        fun getInstance(): ApeniApiService {
            return instance!!
        }

        private fun create(context: Context?): ApeniApiService {

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
//                .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApeniApiService::class.java)
        }
    }


    @GET("get_users.php")
    fun getUsersList(): Call<List<User>>

    @GET("get_obieqts.php")
    fun getObieqts(): Call<List<Obieqti>>

    @GET("get_fasebi.php")
    fun getPrices(): Call<List<ObjToBeerPrice>>

    @GET("get_amonaweri_m.php")
    fun getAmonaweriM(@Query("tarigi") tarigi: String, @Query("objID") objID: Int): Call<List<Amonaweri>>

    @GET("get_amonaweri_k.php")
    fun getAmonaweriK(@Query("tarigi") tarigi: String, @Query("objID") objID: Int): Call<List<Amonaweri>>

    @GET("get_ludi_list.php")
    fun getBeerList(): Call<List<BeerModel>>

    @GET("view_sale_day_v2.php")
    fun getDayInfo(@Query("tarigi") tarigi: String, @Query("distrid") distrid: Int): Call<RealizationDay>

    @POST("del_record_v2.php")
    fun deleteRecord(@Body del: DeleteRequest): Call<SimpleResponce>

    @FormUrlEncoded
    @POST("insert_xarji.php")
    fun addXarji(
        @Field("distrid") userID: String,
        @Field("amount") amount: String,
        @Field("comment") comment: String
    ): Call<SimpleResponce>
}
