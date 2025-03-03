package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.*
import kotlinx.coroutines.launch
/*
class FirestoreViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    fun addUserProfile(user: UserProfile) {
        viewModelScope.launch {
            repository.addUserProfile(user)
        }
    }

    fun addGenre(genre: Genre) {
        viewModelScope.launch {
            repository.addGenre(genre)
        }
    }

    fun addPartyGroup(partyGroup: PartyGroup) {
        viewModelScope.launch {
            repository.addPartyGroup(partyGroup)
        }
    }

    fun addMovieReview(review: MovieReview) {
        viewModelScope.launch {
            repository.addMovieReview(review)
        }
    }
}
 */