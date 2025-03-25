package com.amineaytac.biblictora.core.network.di

import com.amineaytac.biblictora.core.network.source.randomquote.RandomQuoteDataSource
import com.amineaytac.biblictora.core.network.source.randomquote.RandomQuoteDataSourceImpl
import com.amineaytac.biblictora.core.network.source.rest.RestDataSource
import com.amineaytac.biblictora.core.network.source.rest.RestDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SourceModule {

    @Binds
    @Singleton
    abstract fun bindRestDataSource(restDataSourceImpl: RestDataSourceImpl): RestDataSource

    @Binds
    @Singleton
    abstract fun bindRandomQuoteDataSource(randomQuoteDataSourceImpl: RandomQuoteDataSourceImpl): RandomQuoteDataSource
}