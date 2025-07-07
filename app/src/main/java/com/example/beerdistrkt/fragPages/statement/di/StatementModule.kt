package com.example.beerdistrkt.fragPages.statement.di

import com.example.beerdistrkt.fragPages.statement.data.StatementRepositoryImpl
import com.example.beerdistrkt.fragPages.statement.domain.StatementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class StatementModule {

    @Binds
    abstract fun bindUserRepository(
        statementRepositoryImpl: StatementRepositoryImpl
    ): StatementRepository
}