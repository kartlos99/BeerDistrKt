package com.example.beerdistrkt.network.api

import com.example.beerdistrkt.fragPages.beer.data.model.BeerDto
import com.example.beerdistrkt.fragPages.beer.data.model.BeerOrderingUpdateDto
import com.example.beerdistrkt.fragPages.bottle.data.model.BottleDto
import com.example.beerdistrkt.fragPages.bottle.data.model.BottleOrderUpdateDto
import com.example.beerdistrkt.fragPages.customer.data.model.CustomerDTO
import com.example.beerdistrkt.fragPages.customer.data.model.CustomerDeactivationDto
import com.example.beerdistrkt.fragPages.expense.data.model.DeleteExpenseCategoryRequestDto
import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseCategoryDto
import com.example.beerdistrkt.fragPages.expense.data.model.ExpenseDto
import com.example.beerdistrkt.fragPages.homePage.data.model.BaseDataDto
import com.example.beerdistrkt.fragPages.homePage.data.model.CommentDto
import com.example.beerdistrkt.fragPages.login.data.model.LoginDataDto
import com.example.beerdistrkt.fragPages.login.data.model.LoginRequest
import com.example.beerdistrkt.fragPages.login.data.model.UserAuthDto
import com.example.beerdistrkt.fragPages.realisationtotal.data.model.RealizationDayDto
import com.example.beerdistrkt.fragPages.sawyobi.data.StorehouseIoDto
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.fragPages.settings.data.model.SettingParamDto
import com.example.beerdistrkt.fragPages.user.data.model.AddUserRequestModel
import com.example.beerdistrkt.fragPages.user.data.model.BaseInsertApiModel
import com.example.beerdistrkt.fragPages.user.data.model.DeleteRecordApiModel
import com.example.beerdistrkt.fragPages.user.data.model.UserApiModel
import com.example.beerdistrkt.models.CustomerIdlInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DistributionApi {

    @GET("storeHouse/getIoListPaged.php")
    suspend fun getStoreHouseIoPagedList(
        @Query("pageIndex") pageIndex: Int,
        @Query("groupID") groupID: String = ""
    ): List<StorehouseIoDto>

    @GET("storeHouse/getBalance.php")
    suspend fun getStoreHouseBalance(
        @Query("date") date: String,
        @Query("chek") checkFlag: Int
    ): StoreHouseResponse

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

    /* BOTTLE */
    @POST("bottle/updateSortValue.php")
    suspend fun updateBottleSortValue(@Body data: BottleOrderUpdateDto): List<BottleDto>

    @POST("bottle/delete.php")
    suspend fun deleteBottle(@Body bottleId: Int): List<BottleDto>

    @POST("bottle/add.php")
    suspend fun putBottle(@Body bottleDto: BottleDto): List<BottleDto>

    @GET("listing/bottles.php")
    suspend fun getBottles(): List<BottleDto>

    /* customer */
    @GET("listing/customers.php")
    suspend fun getCustomers(): List<CustomerDTO>

    @GET("customer/idleInfo.php")
    suspend fun getIdleInfo(): List<CustomerIdlInfo>

    @POST("customer/add.php")
    suspend fun addCustomer(@Body customerDTO: CustomerDTO): String

    @POST("customer/update.php")
    suspend fun updateCustomer(@Body customerDTO: CustomerDTO): String

    @POST("customer/deactivate.php")
    suspend fun deactivateCustomer(@Body model: CustomerDeactivationDto): String

    /* general */
    @GET("general/getBaseData.php")
    suspend fun getBaseData(): BaseDataDto

    @GET("general/getComments.php")
    suspend fun getComments(): List<CommentDto>

    /* users */
    @GET("listing/users.php")
    suspend fun getUsers(): UserApiModel

    @POST("user/put.php")
    suspend fun putUser(@Body model: AddUserRequestModel): BaseInsertApiModel

    @POST("general/deleteRecord.php")
    suspend fun deleteRecord(@Body deleteModel: DeleteRecordApiModel)

    @POST("user/signIn.php")
    suspend fun signIn(@Body loginRequest: LoginRequest): LoginDataDto

    @POST("user/authenticate.php")
    suspend fun checkAuth(): UserAuthDto

    /* settings */
    @GET("settings/getAll.php")
    suspend fun getSettings(): List<SettingParamDto>

    @POST("settings/updateSetting.php")
    suspend fun updateSetting(@Body settingParamDto: SettingParamDto): SettingParamDto
}