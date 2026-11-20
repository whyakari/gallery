package com.example.gallery.di

import android.content.Context
import androidx.room.Room
import com.example.gallery.data.local.MediaDao
import com.example.gallery.data.local.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMediaDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): MediaDatabase {
        return Room.databaseBuilder(
            context,
            MediaDatabase::class.java,
            "gallery.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMediaDao(database: MediaDatabase): MediaDao {
        return database.mediaDao()
    }
}