package com.example.beerdistrkt.fragPages.settings.di

import com.example.beerdistrkt.fragPages.settings.data.SettingsRepositoryImpl
import com.example.beerdistrkt.fragPages.settings.domain.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}