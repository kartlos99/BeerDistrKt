package com.example.beerdistrkt.fragPages.beer.di

import com.example.beerdistrkt.fragPages.beer.data.BeerRepositoryImpl
import com.example.beerdistrkt.fragPages.beer.domain.BeerRepository
import com.example.beerdistrkt.fragPages.bottle.data.BottleDtoMapper
import com.example.beerdistrkt.fragPages.bottle.data.DefaultBottleDtoMapper
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

    @Binds
    abstract fun bindBottleDtoMapper(
        defaultBottleDtoMapper: DefaultBottleDtoMapper
    ): BottleDtoMapper
}