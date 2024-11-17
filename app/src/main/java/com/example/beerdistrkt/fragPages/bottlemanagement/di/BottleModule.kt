package com.example.beerdistrkt.fragPages.bottlemanagement.di

import com.example.beerdistrkt.fragPages.bottlemanagement.data.BottleRepositoryImpl
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.BottleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class BottleModule {

    @Binds
    abstract fun bindBottleRepository(
        bottleRepositoryImpl: BottleRepositoryImpl
    ): BottleRepository
}