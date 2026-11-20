package com.example.gallery.di

import com.example.gallery.data.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMediaRepository(): MediaRepository {
        // MediaRepository is injected via constructor, so we just return the instance
        // Hilt will handle the injection
        throw UnsupportedOperationException("MediaRepository should be injected via constructor")
    }
}