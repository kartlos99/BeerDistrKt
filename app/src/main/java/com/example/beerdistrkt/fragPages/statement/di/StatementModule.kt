package com.example.beerdistrkt.fragPages.statement.di

import android.content.Context
import androidx.annotation.ColorInt
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.statement.data.StatementRepositoryImpl
import com.example.beerdistrkt.fragPages.statement.domain.StatementRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class StatementModule {

    @Binds
    abstract fun bindUserRepository(
        statementRepositoryImpl: StatementRepositoryImpl
    ): StatementRepository

    companion object {

        const val INPUT_COLOR = "INPUT_COLOR"
        const val OUTPUT_COLOR = "OUTPUT_COLOR"

        @Provides
        @Named(INPUT_COLOR)
        @ColorInt
        fun provideInputColor(
            @ApplicationContext appContext: Context,
        ): Int {
            return appContext.getColor(R.color.red_600)
        }

        @Provides
        @Named(OUTPUT_COLOR)
        @ColorInt
        fun provideOutputColor(
            @ApplicationContext appContext: Context,
        ): Int {
            return appContext.getColor(R.color.green_600)
        }
    }
}