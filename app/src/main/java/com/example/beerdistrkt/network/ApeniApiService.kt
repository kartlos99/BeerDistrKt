package com.example.beerdistrkt.network

import android.content.Context
import com.example.beerdistrkt.BuildConfig
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
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreInsertRequestModel
import com.example.beerdistrkt.fragPages.showHistory.MoneyHistoryDTO
import com.example.beerdistrkt.fragPages.showHistory.OrderHistoryDTO
import com.example.beerdistrkt.fragPages.showHistory.SaleHistoryDTO
import com.example.beerdistrkt.fragPages.sysClear.models.AddClearingModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.utils.Session
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
        ApeniApiService.getHeadersMap().entries.forEach {
            newRequest.addHeader(it.key, it.value)
        }
        return chain.proceed(newRequest.build())
    }

}

interface ApeniApiService {

    companion object {
        private var instance: ApeniApiService? = null

        private const val BASE_URL = BuildConfig.SERVER_URL
//        private const val BASE_URL = "http://192.168.0.102/apeni.localhost.com/tbilisi/mobile/"
//        private const val BASE_URL = "http://172.20.20.137/apeni.localhost.com/tbilisi/mobile/"

        fun getHeadersMap(): Map<String, String> {
            val session = Session.get()
            return if (session.isUserLogged())
                mapOf(
                    "Authorization" to "Bearer ${session.accessToken}",
                    "Client" to "Android",
                    "Region" to session.getRegionID()
                )
            else mapOf("Client" to "Android")
        }

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

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
//                .addCallAdapterFactory()
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

    @GET("statement/getFinancial.php")
    fun getFinancialStatement(
        @Query("offset") offset: Int,
        @Query("clientID") clientID: Int
    ): Call<DataResponse<StatementResponse>>

    @GET("statement/getBarrel.php")
    fun getBarrelStatement(
        @Query("offset") offset: Int,
        @Query("clientID") clientID: Int
    ): Call<DataResponse<StatementResponse>>

    @GET("get_ludi_list.php")
    fun getBeerList(): Call<DataResponse<List<BeerModel>>>

    @GET("get_kasri_list.php")
    fun getCanList(): Call<DataResponse<List<CanModel>>>

    // general
    @GET("general/getComments.php")
    fun getComments(): Call<DataResponse<List<CommentModel>>>

    @POST("general/addComment.php")
    fun addComment(@Body comment: AddCommentModel): Call<DataResponse<String>>

    @POST("general/deleteRecord.php")
    fun deleteRecord(@Body del: DeleteRequest): Call<DataResponse<Any>>

    @POST("getRecord.php")
    fun getRecord(@Body recordModel: RecordRequestModel): Call<DataResponse<RecordResponseDTO>>

    @POST("general/addXarji.php")
    fun addXarji(@Body data: AddXarjiRequestModel): Call<DataResponse<Int>>

    // Orders
    @GET("order/getByDate.php")
    fun getOrders(@Query("date") date: String): Call<DataResponse<List<OrderDTO>>>

    @POST("order/add.php")
    fun addOrder(@Body order: OrderRequestModel): Call<DataResponse<String>>

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

    @GET("order/getHistory.php")
    fun getOrderHistory(@Query("orderID") orderID: String): Call<DataResponse<List<OrderHistoryDTO>>>

    // client
    @GET("client/getDebtByID.php")
    fun getDebt(@Query("clientID") clientID: Int): Call<DataResponse<DebtResponse>>

    @POST("client/add.php")
    fun addClient(@Body obieqti: ObiectWithPrices): Call<DataResponse<String>>

    @POST("client/update.php")
    fun updateClient(@Body obieqti: ObiectWithPrices): Call<DataResponse<String>>

    @POST("client/deactivate.php")
    fun deactivateClient(@Body model: ClientDeactivateModel): Call<DataResponse<String>>

    // storeHouse
    @GET("storeHouse/getBalance.php")
    fun getStoreHouseBalance(
        @Query("date") date: String,
        @Query("chek") chek: Int
    ): Call<DataResponse<StoreHouseResponse>>

    @POST("storeHouse/add.php")
    fun addStoreHouseOperation(@Body StoreHouseAddObject: StoreInsertRequestModel): Call<DataResponse<String>>

    @GET("storeHouse/getioList.php")
    fun getStoreHouseIoList(@Query("groupID") groupID: String): Call<DataResponse<List<IoModel>>>

    // other
    @GET("other/getCleaningList.php")
    fun getSysCleaning(): Call<DataResponse<List<SysClearModel>>>

    @POST("other/addDeleteClearing.php")
    fun addDeleteClearing(@Body data: AddClearingModel): Call<DataResponse<String>>

    // user
    @POST("user/add.php")
    fun addUpdateUser(@Body model: AddUserRequestModel): Call<DataResponse<String>>

    @POST("user/login.php")
    fun logIn(@Body userAndPass: LoginRequest): Call<DataResponse<LoginResponse>>

    @POST("user/changePassword.php")
    fun changePassword(@Body model: ChangePassRequestModel): Call<DataResponse<String>>

    // Sales
    @POST("sales/add.php")
    fun addSales(@Body saleObject: SaleRequestModel): Call<DataResponse<String>>

    @POST("sales/update.php")
    fun updateSale(@Body saleObject: SaleRequestModel): Call<DataResponse<String>>

    @GET("sales/getDayTotal.php")
    fun getDayInfo(
        @Query("tarigi") tarigi: String,
        @Query("distrid") distrid: Int
    ): Call<DataResponse<RealizationDay>>

    @GET("sales/getHistory.php")
    fun getSalesHistory(@Query("saleID") saleID: Int): Call<DataResponse<List<SaleHistoryDTO>>>

    @GET("sales/getMoneyHistory.php")
    fun getMoneyHistory(@Query("recordID") recordID: Int): Call<DataResponse<List<MoneyHistoryDTO>>>
}
