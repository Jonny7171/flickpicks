package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyFeedViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val userRepository: UserProfileRepository
) : ViewModel() {

    private val _trendingMovies = mutableStateOf<List<Movie>>(emptyList())
    val trendingMovies: State<List<Movie>> = _trendingMovies

    private val _moviesReviewedByFriends = mutableStateOf<List<MovieReview>>(emptyList())
    val moviesReviewedByFriends: State<List<MovieReview>> = _moviesReviewedByFriends

    private val _recommendedMovies = mutableStateOf<List<Movie>>(emptyList())
    val recommendedMovies: State<List<Movie>> = _recommendedMovies

    private var _selectedMovie = mutableStateOf<Movie?>(null)
    val selectedMovie: State<Movie?> = _selectedMovie

    private val _watchProviders = mutableStateOf<List<String>>(emptyList())
    val watchProviders: State<List<String>> = _watchProviders

    init {
        viewModelScope.launch {
            _trendingMovies.value = repository.getTrendingMovies()
        }
    }

    fun fetchReviewedByFriends(userId: Int) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            val friends = userProfile?.friends ?: emptyList()

            val friendReviews = mutableListOf<MovieReview>()
            for (friendId in friends) {
                val friendProfile = userRepository.getUserProfile(friendId.toInt())
                friendProfile?.moviesReviewed?.forEach { review ->
//                    friendReviews.add(review)
                }
            }

            _moviesReviewedByFriends.value = friendReviews
        }
    }

    fun fetchRecommendedMovies(userId: Int) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            // moviesPreferences should be genrePreferences
            val preferredGenres = userProfile?.moviesPreferences ?: emptyList()

            if (preferredGenres.isNotEmpty()) {
                val recommendedMovies = repository.getMoviesByGenres(preferredGenres)
                _recommendedMovies.value = recommendedMovies
            }
        }
    }

    suspend fun getMovieDetails(movieId: String) {
        _selectedMovie.value = repository.getMovieDetails(movieId)
    }

    suspend fun fetchWatchProviders(movieId: String) {
        _watchProviders.value = repository.getMovieWatchProviders(movieId)
    }
}

