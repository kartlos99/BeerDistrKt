package com.example.beerdistrkt.fragPages.homePage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.homePage.models.CommentModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.*
import java.util.*

class HomeViewModel : BaseViewModel() {

    private val usersLiveData = database.getUsers()
    private val beerLiveData = database.getBeerList()
    private val cansLiveData = database.getCansList()
    lateinit var beerList: List<BeerModel>

    var localVersionState: VcsResponse? = null
    var numberOfUpdatingTables = 0

    val mainLoaderLiveData = MutableLiveData<Boolean?>(null)

    private var currentDate = Calendar.getInstance()
    private val _barrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val barrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _barrelsListLiveData

    private val _commentsListLiveData = MutableLiveData<List<CommentModel>>()
    val commentsListLiveData: LiveData<List<CommentModel>>
        get() = _commentsListLiveData

    init {
        if (Session.get().isUserLogged())
            getTableVersionsFromServer()
        localVersionState = SharedPreferenceDataSource.getInstance().getVersions()
        Log.d("localVers", localVersionState.toString())

        beerLiveData.observeForever {
            beerList = it
            ObjectCache.getInstance().putList(BeerModel::class, "beerList", it)
        }
        cansLiveData.observeForever {
            ObjectCache.getInstance().putList(CanModel::class, "canList", it)
        }
        usersLiveData.observeForever { userList ->
            ObjectCache.getInstance()
                .putList(User::class, "userList", userList.sortedBy { it.name })
        }
    }

    private fun getTableVersionsFromServer() {
        sendRequest(
            ApeniApiService.getInstance().getTableVersions(),
            successWithData = {
                if (localVersionState != null) {
                    if (it.beer > localVersionState!!.beer) {
                        getBeerList()
                        numberOfUpdatingTables++
                    }
                    if (it.client > localVersionState!!.client) {
                        getObjects()
                        numberOfUpdatingTables++
                    }
                    if (it.user > localVersionState!!.user) {
                        getUsers()
                        numberOfUpdatingTables++
                    }
                    if (it.barrel > localVersionState!!.barrel) {
                        getCanTypes()
                        numberOfUpdatingTables++
                    }
                    if (it.price > localVersionState!!.price) {
                        getPrices()
                        numberOfUpdatingTables++
                    }
                } else {
                    numberOfUpdatingTables = 5
                    getObjects()
                    getPrices()
                    getUsers()
                    getBeerList()
                    getCanTypes()
                }
                mainLoaderLiveData.value = numberOfUpdatingTables > 0
                localVersionState = it
            }
        )
    }

    private fun saveVersion() {
        numberOfUpdatingTables--
        if (numberOfUpdatingTables == 0 && localVersionState != null) {
            mainLoaderLiveData.value = false
            SharedPreferenceDataSource.getInstance().saveVersions(localVersionState!!)
        }
    }

    private fun clearObieqtsList() {
        ioScope.launch {
            database.clearObiectsTable()
        }
    }

    private fun getUsers() {
        sendRequest(
            ApeniApiService.getInstance().getUsersList(),
            successWithData = {
                saveVersion()
                Log.d(TAG, "Users_respOK")
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearUserTable()
                        it.forEach { user ->
                            insertUserToDB(user)
                        }
                    }
                }
            }
        )
    }

    private fun getBeerList() {
        sendRequest(
            ApeniApiService.getInstance().getBeerList(),
            successWithData = {
                saveVersion()
                Log.d(TAG, "Users_respOK")
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearBeerTable()
                        it.forEach { beer ->
                            insertBeerToDB(beer)
                        }
                    }
                }
            }
        )
    }

    private fun getPrices() {
        sendRequest(
            ApeniApiService.getInstance().getPrices(),
            successWithData = {
                Log.d(TAG, "price_respOK")
                saveVersion()
                clearPrices()
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        it.forEach { bPrice ->
                            insertBeetPrice(bPrice)
                        }
                    }
                }
            }
        )
    }

    private fun getObjects() {
        sendRequest(
            ApeniApiService.getInstance().getObieqts(),
            successWithData = {
                Log.d(TAG, "clients_respOK")
                saveVersion()
                clearObieqtsList()
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        it.forEach { obieqti ->
                            insertObiect(obieqti)
                        }
                    }
                }
            }
        )
    }

    private fun getCanTypes() {
        sendRequest(
            ApeniApiService.getInstance().getCanList(),
            successWithData = {
                saveVersion()
                Log.d(TAG, "Cans_respOK")
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearCansTable()
                        it.forEach { can ->
                            insertCanToDB(can)
                        }
                    }
                }
            }
        )
    }

    private fun insertUserToDB(user: User) {
        Log.d(TAG, user.toString())
        database.insertUser(user)
    }

    private fun insertBeerToDB(beerModel: BeerModel) {
        Log.d(TAG, beerModel.toString())
        database.insertBeer(beerModel)
    }

    private fun insertObiect(obieqti: Obieqti) {
        database.insertObiecti(obieqti)
    }

    private fun insertBeetPrice(bPrice: ObjToBeerPrice) {
        database.insertBeerPrice(bPrice)
    }

    private fun insertCanToDB(canModel: CanModel) {
        database.insertCan(canModel)
    }

    private fun clearPrices() {
        ioScope.launch {
            database.clearPricesTable()
        }
    }

    fun getStoreBalance() {
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseBalance(
                dateFormatDash.format(currentDate.time), 0
            ),
            successWithData = {
                Log.d("store", it.empty.toString())
                formAllBarrelsList(it)
            }
        )
    }

    private fun formAllBarrelsList(data: StoreHouseResponse) {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = data.full.groupBy { it.beerID }
        a.values.forEach {
            val valueOfDiff = mutableMapOf<Int, Int>()
            it.forEach { fbm ->
                valueOfDiff[fbm.barrelID] = fbm.inputToStore - fbm.saleCount
            }
            val title = beerList.first { b -> b.id == it[0].beerID }.dasaxeleba ?: "_"
            result.add(SimpleBeerRowModel(title, valueOfDiff))
        }
        val valueOfDiff = mutableMapOf<Int, Int>()
        data.empty?.forEach { ebm ->
            valueOfDiff[ebm.barrelID] = ebm.inputEmptyToStore - ebm.outputEmptyFromStoreCount
        }
        result.add(SimpleBeerRowModel("ცარიელი", valueOfDiff))

        _barrelsListLiveData.value = result
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared job dacenselda")
        job.cancel()
    }

    fun getComments() {
        sendRequest(
            ApeniApiService.getInstance().getcomments(),
            successWithData = {
                _commentsListLiveData.value = it
            }
        )
    }

    companion object {
        const val TAG = "homeVM"
    }
}