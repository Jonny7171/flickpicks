package com.example.flickpicks.data.source

import com.example.flickpicks.data.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://movies-app-backend.replit.app/"

interface MoviesSource {
    @GET("api/genres")
    suspend fun getGenres(): List<List<Any>>

    @GET("api/movies")
    suspend fun getMovies(@Query("genre") genre: String? = null):List<Movie>
}