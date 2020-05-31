package com.example.beerdistrkt.network

import android.content.Context
import com.example.beerdistrkt.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


interface ApeniApiService {

    companion object {
        private var instance: ApeniApiService? = null

//        const val BASE_URL = "https://apeni.ge/tbilisi/mobile/"
        private const val BASE_URL = "http://192.168.1.104/apeni.localhost.com/tbilisi/mobile/"

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
    fun getUsersList(): Call<DataResponse<List<User>>>

    @GET("get_obieqts.php")
    fun getObieqts(): Call<DataResponse<List<Obieqti>>>

    @GET("get_fasebi.php")
    fun getPrices(): Call<DataResponse<List<ObjToBeerPrice>>>

    @GET("get_amonaweri_m.php")
    fun getAmonaweriM(@Query("tarigi") tarigi: String, @Query("objID") objID: Int): Call<DataResponse<List<Amonaweri>>>

    @GET("get_amonaweri_k.php")
    fun getAmonaweriK(@Query("tarigi") tarigi: String, @Query("objID") objID: Int): Call<DataResponse<List<Amonaweri>>>

    @GET("get_ludi_list.php")
    fun getBeerList(): Call<DataResponse<List<BeerModel>>>

    @GET("view_sale_day_v2.php")
    fun getDayInfo(@Query("tarigi") tarigi: String, @Query("distrid") distrid: Int): Call<DataResponse<RealizationDay>>

    @POST("del_record_v2.php")
    fun deleteRecord(@Body del: DeleteRequest): Call<DataResponse<Any>>

    @FormUrlEncoded
    @POST("insert_xarji.php")
    fun addXarji(
        @Field("distrid") userID: String,
        @Field("amount") amount: String,
        @Field("comment") comment: String
    ): Call<DataResponse<Int>>

    @GET("order/getByDate.php")
    fun getOrders(@Query("date") date: String): Call<DataResponse<List<OrderDTO>>>
}
