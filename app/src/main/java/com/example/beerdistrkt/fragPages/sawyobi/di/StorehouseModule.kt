package com.example.beerdistrkt.fragPages.sawyobi.di

import com.example.beerdistrkt.network.api.StorehouseIoRepositoryImpl
import com.example.beerdistrkt.network.api.StorehouseIoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class StorehouseModule {

    @Binds
    abstract fun bindBeerRepository(
        repositoryImpl: StorehouseIoRepositoryImpl
    ): StorehouseIoRepository

}