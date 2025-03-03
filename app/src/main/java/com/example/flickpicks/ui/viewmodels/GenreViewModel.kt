package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.Genre
import com.example.flickpicks.data.repository.GenreFirestoreDatabase
import com.example.flickpicks.data.repository.GenreRepository
import kotlinx.coroutines.launch

class GenreViewModel: ViewModel() {
        val repository = GenreRepository(GenreFirestoreDatabase())
     fun addGenre(genre: Genre) {
        viewModelScope.launch {
            repository.addGenre(genre)
        }
    }
    fun getGenre(genre: String) {
        viewModelScope.launch {
            repository.getGenre(genre.toString())
        }
    }
    fun deleteGenre(genre: Genre) {
        viewModelScope.launch {
            repository.deleteGenre(genre.toString())
        }
    }
    fun updateGenre(genre: Genre, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updateGenre(genre, updates)
        }
    }
}

