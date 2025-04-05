package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MoviesRepository,
    private val userRepository: UserProfileRepository,
    ) : ViewModel() {

    private val _searchResults = mutableStateOf<List<Movie>>(emptyList())
    val searchResults: State<List<Movie>> = _searchResults

    private val _likedMovies = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val likedMovies: State<Map<String, Boolean>> get() = _likedMovies

    private val _dislikedMovies = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val dislikedMovies: State<Map<String, Boolean>> get() = _dislikedMovies

    private val _savedMovies = mutableStateOf<Map<String, Boolean>>(emptyMap())
    val savedMovies: State<Map<String, Boolean>> get() = _savedMovies

    fun searchMovies(query:String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchMovie(query)
        }
    }
    suspend fun getTrailer(movieId: String): String? {
        return repository.getMovieTrailer(movieId)
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
                    this[movieName] = !this.getOrDefault(movieName, false)
                }.toMap()
            }
        }
    }
}

