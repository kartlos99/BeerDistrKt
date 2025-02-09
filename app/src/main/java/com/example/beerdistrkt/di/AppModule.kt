package com.example.beerdistrkt.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.beerdistrkt.BuildConfig
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.AuthHelper
import com.example.beerdistrkt.network.SessionInterceptor
import com.example.beerdistrkt.network.UserAuthenticator
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.utils.LogoutUtil
import com.example.beerdistrkt.utils.Session
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// value may be changed late
private const val PREFERENCE_FILE = "order_folds_store"

private const val DB_NAME = "apeni_db"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSession(
        userPreferencesRepository: UserPreferencesRepository,
    ): Session {
        return Session(userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        session: Session
    ): SessionInterceptor {
        return SessionInterceptor(session)
    }

    @Singleton
    @Provides
    fun provideLogoutUtil(
        @ApplicationContext appContext: Context,
        session: Session,
    ): LogoutUtil = LogoutUtil(
        appContext,
        session,
    )

    @Singleton
    @Provides
    fun provideAuthHelper(
        logoutUtil: LogoutUtil,
    ): AuthHelper = AuthHelper(logoutUtil)

    @Singleton
    @Provides
    fun provide(
        authHelper: AuthHelper,
    ): UserAuthenticator {
        return UserAuthenticator(authHelper)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionInterceptor: SessionInterceptor,
        userAuthenticator: UserAuthenticator,
    ): OkHttpClient = OkHttpClient.Builder()
        .authenticator(userAuthenticator)
        .addInterceptor(sessionInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    @Provides
    @Singleton
    fun provideJsonSerializer(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideDistributionApi(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): DistributionApi = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.MOBILE_API_URL)
        .build()
        .create(DistributionApi::class.java)

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext, PREFERENCE_FILE)),
            produceFile = { appContext.preferencesDataStoreFile(PREFERENCE_FILE) }
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        dataStore: DataStore<Preferences>,
        moshi: Moshi,
    ): UserPreferencesRepository {
        return UserPreferencesRepository(
            dataStore = dataStore,
            moshi = moshi,
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): ApeniDatabaseDao {
        val db = Room.databaseBuilder(
            appContext.applicationContext,
            ApeniDataBase::class.java,
            DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        return db.apeniDataBaseDao
    }


    @Provides
    @Singleton
    fun provideApeniApi(
        sessionInterceptor: SessionInterceptor,
    ): ApeniApiService {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(sessionInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
//                .addCallAdapterFactory()
            .baseUrl(BuildConfig.SERVER_URL)
            .build()

        return retrofit.create(ApeniApiService::class.java)
    }
}