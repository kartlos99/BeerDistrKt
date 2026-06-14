package com.example.beerdistrkt.fragPages.barrel.di

import com.example.beerdistrkt.fragPages.barrel.data.EmptyBarrelRepositoryImpl
import com.example.beerdistrkt.fragPages.barrel.domain.EmptyBarrelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class BarrelModule {

    @Binds
    abstract fun bind(
        repoImpl: EmptyBarrelRepositoryImpl
    ): EmptyBarrelRepository
}