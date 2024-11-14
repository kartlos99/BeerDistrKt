package com.example.beerdistrkt.fragPages.homePage.di

import com.example.beerdistrkt.fragPages.homePage.data.HomeRepositoryImpl
import com.example.beerdistrkt.fragPages.homePage.domain.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class HomeModule {

    @Binds
    abstract fun bindHomeRepository(
        repositoryImpl: HomeRepositoryImpl
    ): HomeRepository
}