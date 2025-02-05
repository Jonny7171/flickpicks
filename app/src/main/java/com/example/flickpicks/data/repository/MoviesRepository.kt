package com.example.flickpicks.data.repository

import com.example.flickpicks.data.model.Genre
import com.example.flickpicks.data.source.MoviesSource
import javax.inject.Inject

class MoviesRepository @Inject constructor(
    private val moviesSource: MoviesSource
){
    suspend fun getGenres(): List<Genre> {
        return moviesSource.getGenres().map{
            val name = it[0].toString()
            val count = (it[1] as Double).toInt()
            Genre(name, count)
        }
    }
    suspend fun getMovies(genre: String?) = moviesSource.getMovies(genre)
}


