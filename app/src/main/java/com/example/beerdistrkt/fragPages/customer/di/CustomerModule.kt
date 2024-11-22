package com.example.beerdistrkt.fragPages.customer.di

import com.example.beerdistrkt.fragPages.customer.data.CustomerRepositoryImpl
import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class CustomerModule {

    @Binds
    abstract fun bindCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository
}