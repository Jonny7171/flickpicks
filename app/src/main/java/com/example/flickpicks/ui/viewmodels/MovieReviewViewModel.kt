package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.repository.MovieReviewFirestoreDatabase
import com.example.flickpicks.data.repository.MovieReviewRepository
import kotlinx.coroutines.launch

class MovieReviewViewModel: ViewModel() {
    val repository = MovieReviewRepository(MovieReviewFirestoreDatabase())
    fun addMovieReview(movieReview: MovieReview) {
        viewModelScope.launch {
            repository.addMovieReview(movieReview)
        }
    }
    fun getMovieReview(reviewId: Int) {
        viewModelScope.launch {
            repository.getMovieReview(reviewId)
        }
    }
    fun deleteMovieReview(movieReview: MovieReview) {
        viewModelScope.launch {
            repository.deleteMovieReview(movieReview.id)
        }
    }
    fun updateMovieReview(movieReview: MovieReview, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updateMovieReview(movieReview, updates)
        }
    }
}


