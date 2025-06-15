package com.example.beerdistrkt.fragPages.login.di

import com.example.beerdistrkt.fragPages.login.data.AuthRepositoryImpl
import com.example.beerdistrkt.fragPages.login.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class LoginModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}