package com.example.flickpicks.di

import android.content.Context
import com.example.flickpicks.data.database.SessionDao
import com.example.flickpicks.data.database.UserSessionDB
import com.example.flickpicks.data.repository.MovieReviewDatabase
import com.example.flickpicks.data.repository.MovieReviewFirestoreDatabase
import com.example.flickpicks.data.repository.UserProfileDatabase
import com.example.flickpicks.data.repository.UserProfileFirestoreDatabase
import com.example.flickpicks.data.repository.UserSessionRepository
import com.example.flickpicks.data.source.MoviesSource
import com.example.flickpicks.ui.viewmodels.AuthManager
import com.example.flickpicks.ui.viewmodels.FirebaseAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideMovieReviewDatabase(): MovieReviewDatabase {
        return MovieReviewFirestoreDatabase()
    }


    @Provides
    @Singleton
    fun provideSessionDatabase(@ApplicationContext context: Context): UserSessionDB {
        return UserSessionDB.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSessionDao(db: UserSessionDB): SessionDao {
        return db.sessionDao()
    }

    @Provides
    @Singleton
    fun provideSessionRepository(dao: SessionDao): UserSessionRepository {
        return UserSessionRepository(dao)
    }

    @Provides
    fun provideAuthManager(): AuthManager = FirebaseAuthManager()
}