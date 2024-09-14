package com.example.beerdistrkt.fragPages.realisationtotal.di

import com.example.beerdistrkt.fragPages.realisationtotal.data.model.RealizationRepositoryImpl
import com.example.beerdistrkt.fragPages.realisationtotal.domain.RealizationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RealizationModule {

    @Binds
    abstract fun bindRealizationRepository(
        repositoryImpl: RealizationRepositoryImpl
    ): RealizationRepository
}