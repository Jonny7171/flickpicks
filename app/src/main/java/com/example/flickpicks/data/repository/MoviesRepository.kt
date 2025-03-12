package com.example.flickpicks.data.repository

import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.source.MoviesSource
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val moviesSource: MoviesSource
) {
    suspend fun getTrendingMovies(): List<Movie> = moviesSource.getTrendingMovies()

    suspend fun getMovieDetails(movieId: String): Movie = moviesSource.getMovieDetails(movieId)

    suspend fun getMovieWatchProviders(
        movieId: String
    ): List<String> = moviesSource.getMovieWatchProviders(movieId)

    suspend fun getMoviesByGenres(
        genreIds: List<String>
    ): List<Movie> = moviesSource.getMoviesByGenres(genreIds)

    suspend fun getMovieTrailer(
        movieId: String
    ) : String? = moviesSource.getMovieTrailer(movieId)
}
