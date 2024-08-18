package com.example.beerdistrkt.fragPages.expense.di

import com.example.beerdistrkt.fragPages.expense.data.ExpenseRepositoryImpl
import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ExpenseModule {

    @Binds
    abstract fun bindExpenseRepository(
        repositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository
}