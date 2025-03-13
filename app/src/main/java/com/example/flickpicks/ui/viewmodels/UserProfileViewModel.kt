package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.UserProfileFirestoreDatabase
import com.example.flickpicks.data.repository.UserProfileRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(): ViewModel() {
    val repository = UserProfileRepository(UserProfileFirestoreDatabase())
    private val db = Firebase.firestore

    private val _userProfile = mutableStateOf<UserProfile?>(null)
    val userProfile: State<UserProfile?> = _userProfile

    fun addUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.addUserProfile(profile)
        }
    }
    fun getUserProfile(profileId: String) {
        viewModelScope.launch {
            repository.getUserProfile(profileId)
        }
    }
    fun deleteUserProfile(profileId: String) {
        viewModelScope.launch {
            repository.deleteUserProfile(profileId)
        }
    }
    fun updateUserProfile(profileId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updateUserProfile(profileId, updates)
        }
    }
    fun fetchUserProfile(profileId: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfile(profileId)
            _userProfile.value = profile
        }
    }
    suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("userName", username)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}


