package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MoviesRepository,
) : ViewModel() {

    private val _searchResults = mutableStateOf<List<Movie>>(emptyList())
    val searchResults: State<List<Movie>> = _searchResults

    fun searchMovies(query:String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchMovie(query)
        }
    }
    suspend fun getTrailer(movieId: String): String? {
        return repository.getMovieTrailer(movieId)
    }
}

