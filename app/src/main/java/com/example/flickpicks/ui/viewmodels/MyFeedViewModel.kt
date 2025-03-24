package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.GENRE_MAP
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.repository.MovieReviewRepository
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.collections.Map
import kotlinx.coroutines.launch
import javax.inject.Inject

var reviewIdsMap = mutableMapOf<Int, Boolean>()
var currReviewId = 0

@HiltViewModel
class MyFeedViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val userRepository: UserProfileRepository,
    private val reviewRepository: MovieReviewRepository
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

    private val _likedMovies = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val likedMovies: State<Map<String, Boolean>> get() = _likedMovies

    private val _dislikedMovies = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val dislikedMovies: State<Map<String, Boolean>> get() = _dislikedMovies

    private val _savedMovies = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val savedMovies: State<Map<String, Boolean>> get() = _savedMovies

    init {
        viewModelScope.launch {
            _trendingMovies.value = repository.getTrendingMovies()
        }
    }

    fun fetchReviewedByFriends(userId: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            val friends = userProfile?.followers ?: emptyList()
            val friendReviews = mutableListOf<MovieReview>()

            for (friendId in friends) {
                val friendProfile = userRepository.getUserProfile(friendId)
                friendProfile?.moviesReviewed?.forEach { review ->
                    friendReviews.add(review)
                }
            }

            _moviesReviewedByFriends.value = friendReviews
        }
    }

    fun fetchRecommendedMovies(userId: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            val preferredGenres = userProfile?.genrePreferences ?: emptyList()

            val genreIds = preferredGenres.mapNotNull { genreName ->
                GENRE_MAP[genreName.lowercase()]
            }

            if (genreIds.isNotEmpty()) {
                val recommendedMovies = repository.getMoviesByGenres(genreIds)
                _recommendedMovies.value = recommendedMovies
            }
        }
    }

    fun fetchMoviesState(userId: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfile?.let {
                _likedMovies.value = it.moviesLiked.associateWith { true }
                _dislikedMovies.value = it.moviesDisliked.associateWith { true }
                _savedMovies.value = it.moviesSaved.associateWith { true }
            }
        }
    }

    fun saveLikedMovie(userId: String, movieName: String, remove: Boolean) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfile?.let {
                val updatedMoviesLiked = it.moviesLiked.toMutableList().apply {
                    if (remove) {
                        remove(movieName)
                    } else {
                        if (!contains(movieName)) add(movieName)
                    }
                }
                userRepository.updateUserProfile(it.id, mapOf("moviesLiked" to updatedMoviesLiked))
                _likedMovies.value = _likedMovies.value.toMutableMap().apply {
                    this[movieName] = !remove
                }
            }
        }
    }

    fun saveDislikedMovie(userId: String, movieName: String, remove: Boolean) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfile?.let {
                val updatedMoviesDisliked = it.moviesDisliked.toMutableList().apply {
                    if (remove) {
                        remove(movieName)
                    } else {
                        if (!contains(movieName)) add(movieName)
                    }
                }
                userRepository.updateUserProfile(it.id, mapOf("moviesDisliked" to updatedMoviesDisliked))
                _dislikedMovies.value = _dislikedMovies.value.toMutableMap().apply {
                    this[movieName] = !remove
                }
            }
        }
    }

    fun saveMovie(userId: String, movieName: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            userProfile?.let {
                val updatedMoviesSaved = it.moviesSaved.toMutableList().apply {
                    if (contains(movieName)) remove(movieName) else add(movieName)
                }
                userRepository.updateUserProfile(it.id, mapOf("moviesSaved" to updatedMoviesSaved))
                _savedMovies.value = _savedMovies.value.toMutableMap().apply {
                    this[movieName] = !contains(movieName)
                }
            }
        }
    }

    fun postReview(userId: String, movieId: String, rating: String, review: String, whereWatched: String) {
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile(userId)
            val movie = repository.getMovieDetails(movieId)

            userProfile?.let {
                val existingReview = it.moviesReviewed.find { review -> review.movieId == movieId }
                val reviewId: Int

                if (existingReview != null) {
                    reviewId = existingReview.id
                    reviewRepository.deleteMovieReview(existingReview.id)
                } else {
                    reviewId = currReviewId
                    reviewIdsMap[reviewId] = true
                    currReviewId++
                }

                val newReview = MovieReview(
                    id = reviewId,
                    movieId = movieId,
                    movieTitle = movie.title ?: "",
                    release_date = movie.release_date ?: "",
                    tagline = movie.tagline ?: "",
                    overview = movie.overview ?: "",
                    genres = movie.genres ?: listOf(),
                    reviewerName = it.userName,
                    reviewText = review,
                    rating = rating.toIntOrNull() ?: 0,
                    streamingPlatform = whereWatched
                )
                val updatedReviews = it.moviesReviewed.toMutableList().apply {
                    if (existingReview != null) {
                        remove(existingReview)
                    }
                    add(newReview)
                }
                reviewRepository.addMovieReview(newReview)
                userRepository.updateUserProfile(it.id, mapOf("moviesReviewed" to updatedReviews))
            }
        }
    }

    suspend fun getMovieDetails(movieId: String) {
        _selectedMovie.value = repository.getMovieDetails(movieId)
    }

    suspend fun fetchWatchProviders(movieId: String) {
        _watchProviders.value = repository.getMovieWatchProviders(movieId)
    }

    suspend fun getTrailer(movieId: String): String? {
        return repository.getMovieTrailer(movieId)
    }

    suspend fun getMovieReviews(movieId: String): List<Pair<String, String>>? {
        return repository.getMovieReviews(movieId)
    }
}

