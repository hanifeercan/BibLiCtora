package com.amineaytac.biblictora.core.database.di

import android.content.Context
import androidx.room.Room
import com.amineaytac.biblictora.core.database.BiblictoraDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BiblictoraDatabase = Room.databaseBuilder(
        context = context,
        klass = BiblictoraDatabase::class.java,
        name = "biblictora_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideDao(database: BiblictoraDatabase) = database.favoriteDao()

    @Provides
    fun provideReadingDao(database: BiblictoraDatabase) = database.readingStatusDao()

    @Provides
    fun provideQuotesDao(database: BiblictoraDatabase) = database.quotesDao()

    @Provides
    fun provideMyBooksDao(database: BiblictoraDatabase) = database.myBooksDao()
}