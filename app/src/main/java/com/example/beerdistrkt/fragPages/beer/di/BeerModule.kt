package com.example.beerdistrkt.fragPages.beer.di

import com.example.beerdistrkt.fragPages.beer.data.BeerRepositoryImpl
import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class BeerModule {

    @Binds
    abstract fun bindBeerRepository(
        repositoryImpl: BeerRepositoryImpl
    ): BeerRepository
}