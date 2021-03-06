package com.example.beerdistrkt.network

import android.content.Context
import com.example.beerdistrkt.fragPages.addEditUser.models.AddUserRequestModel
import com.example.beerdistrkt.fragPages.homePage.models.AddCommentModel
import com.example.beerdistrkt.fragPages.homePage.models.CommentModel
import com.example.beerdistrkt.fragPages.login.models.LoginRequest
import com.example.beerdistrkt.fragPages.login.models.LoginResponse
import com.example.beerdistrkt.fragPages.mitana.models.RecordRequestModel
import com.example.beerdistrkt.fragPages.mitana.models.RecordResponseDTO
import com.example.beerdistrkt.fragPages.orders.models.OrderDeleteRequestModel
import com.example.beerdistrkt.fragPages.orders.models.OrderReSortModel
import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.fragPages.orders.models.OrderUpdateDistributorRequestModel
import com.example.beerdistrkt.fragPages.sales.models.AddXarjiRequestModel
import com.example.beerdistrkt.fragPages.sales.models.SaleRequestModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreInsertRequestModel
import com.example.beerdistrkt.fragPages.sysClear.models.AddClearingModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.models.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

//        private const val BASE_URL = "https://apeni.ge/tbilisi/mobile/"
        private const val BASE_URL = "http://192.168.0.102/apeni.localhost.com/tbilisi/mobile/"

//        test at server
//        private const val BASE_URL = TEST_SERVER_URL

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
                .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApeniApiService::class.java)
        }
    }

    @GET("getVersions.php")
    fun getTableVersions(): Call<DataResponse<VcsResponse>>

    @GET("get_users.php")
    fun getUsersList(): Call<DataResponse<List<User>>>

    @GET("get_obieqts.php")
    fun getObieqts(): Call<DataResponse<List<Obieqti>>>

    @GET("get_fasebi.php")
    fun getPrices(): Call<DataResponse<List<ObjToBeerPrice>>>

    @GET("get_amonaweri_m.php")
    fun getAmonaweriM(
        @Query("tarigi") tarigi: String,
        @Query("objID") objID: Int
    ): Call<DataResponse<List<Amonaweri>>>

    @GET("get_amonaweri_k.php")
    fun getAmonaweriK(
        @Query("tarigi") tarigi: String,
        @Query("objID") objID: Int
    ): Call<DataResponse<List<Amonaweri>>>

    @GET("get_ludi_list.php")
    fun getBeerList(): Call<DataResponse<List<BeerModel>>>

    @GET("get_kasri_list.php")
    fun getCanList(): Call<DataResponse<List<CanModel>>>

    @GET("sales/getDayTotal.php")
    fun getDayInfo(
        @Query("tarigi") tarigi: String,
        @Query("distrid") distrid: Int
    ): Call<DataResponse<RealizationDay>>

    @POST("general/deleteRecord.php")
    fun deleteRecord(@Body del: DeleteRequest): Call<DataResponse<Any>>

    @POST("getRecord.php")
    fun getRecord(@Body recordModel: RecordRequestModel): Call<DataResponse<RecordResponseDTO>>

    @POST("general/addXarji.php")
    fun addXarji(@Body data: AddXarjiRequestModel): Call<DataResponse<Int>>

    @GET("order/getByDate.php")
    fun getOrders(@Query("date") date: String): Call<DataResponse<List<OrderDTO>>>

    @POST("order/add.php")
    fun addOrder(@Body order: OrderRequestModel): Call<DataResponse<String>>

    @POST("sales/add.php")
    fun addSales(@Body saleObject: SaleRequestModel): Call<DataResponse<String>>

    @POST("sales/update.php")
    fun updateSale(@Body saleObject: SaleRequestModel): Call<DataResponse<String>>

    @GET("order/getByID.php")
    fun getOrderByID(@Query("orderID") orderID: Int): Call<DataResponse<List<OrderDTO>>>

    @POST("order/delete.php")
    fun deleteOrder(@Body deleteRequestModel: OrderDeleteRequestModel): Call<DataResponse<Any>>

    @POST("order/update.php")
    fun updateOrder(@Body order: OrderRequestModel): Call<DataResponse<String>>

    @GET("order/getLastActiveID.php")
    fun getLastActiveOrderID(@Query("clientID") clientID: Int): Call<DataResponse<Int>>

    @POST("order/updateSortValue.php")
    fun updateOrderSortValue(@Body data: OrderReSortModel): Call<DataResponse<String>>

    @POST("order/updateDistributor.php")
    fun updateOrderDistributor(@Body data: OrderUpdateDistributorRequestModel): Call<DataResponse<String>>

    // client
    @GET("client/getDebtByID.php")
    fun getDebt(@Query("clientID") clientID: Int): Call<DataResponse<DebtResponse>>

    @POST("client/login.php")
    fun logIn(@Body userAndPass: LoginRequest): Call<DataResponse<LoginResponse>>

    @POST("client/add.php")
    fun addClient(@Body obieqti: ObiectWithPrices): Call<DataResponse<String>>

    @POST("client/update.php")
    fun updateClient(@Body obieqti: ObiectWithPrices): Call<DataResponse<String>>

    // storeHouse
    @GET("storeHouse/getBalance.php")
    fun getStoreHouseBalance(
        @Query("date") date: String,
        @Query("chek") chek: Int
    ): Call<DataResponse<StoreHouseResponse>>

    @POST("storeHouse/add.php")
    fun addStoreHouseOperation(@Body StoreHouseAddObject: StoreInsertRequestModel): Call<DataResponse<String>>

    // general
    @GET("general/getComments.php")
    fun getcomments(): Call<DataResponse<List<CommentModel>>>

    @POST("general/addComment.php")
    fun addComment(@Body comment: AddCommentModel): Call<DataResponse<String>>

    // other
    @GET("other/getCleaningList.php")
    fun getSysCleaning(): Call<DataResponse<List<SysClearModel>>>

    @POST("other/addDeleteClearing.php")
    fun addDeleteClearing(@Body data: AddClearingModel): Call<DataResponse<String>>

    // user
    @POST("user/add.php")
    fun addUpdateUser(@Body model: AddUserRequestModel): Call<DataResponse<String>>

    @POST("user/changePassword.php")
    fun changePassword(@Body model: ChangePassRequestModel): Call<DataResponse<String>>
}
