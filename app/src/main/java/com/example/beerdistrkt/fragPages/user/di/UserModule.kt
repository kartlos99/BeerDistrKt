package com.example.beerdistrkt.fragPages.user.di

import com.example.beerdistrkt.fragPages.user.data.UserRepositoryImpl
import com.example.beerdistrkt.fragPages.user.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class UserModule {

    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}