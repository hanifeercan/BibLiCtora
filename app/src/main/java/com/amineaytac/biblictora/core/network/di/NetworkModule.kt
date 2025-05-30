package com.amineaytac.biblictora.core.network.di

import com.amineaytac.biblictora.core.network.source.randomquote.RandomQuoteApi
import com.amineaytac.biblictora.core.network.source.rest.BookRestApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RestApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QuoteApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val REST_API_BASE_URL = "https://gutendex.com/books/"
    private const val QUOTES_API_BASE_URL = "https://quotes-api-self.vercel.app/quote/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor).build()
    }

    @Provides
    @Singleton
    @RestApi
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(REST_API_BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun provideBookRestApi(@RestApi retrofit: Retrofit): BookRestApi {
        return retrofit.create(BookRestApi::class.java)
    }

    @Provides
    @Singleton
    @QuoteApi
    fun provideRandomQuoteRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(QUOTES_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun providesRandomQuoteApi(@QuoteApi retrofit: Retrofit): RandomQuoteApi {
        return retrofit.create(RandomQuoteApi::class.java)
    }
}