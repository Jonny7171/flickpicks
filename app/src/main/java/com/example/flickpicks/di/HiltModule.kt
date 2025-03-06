package com.example.flickpicks.di

import com.example.flickpicks.data.repository.UserProfileDatabase
import com.example.flickpicks.data.repository.UserProfileFirestoreDatabase
import com.example.flickpicks.data.source.MoviesSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideMoviesSource(): MoviesSource {
        return MoviesSource()
    }

    @Provides
    @Singleton
    fun provideUserProfileDatabase(): UserProfileDatabase {
        return UserProfileFirestoreDatabase()
    }
}