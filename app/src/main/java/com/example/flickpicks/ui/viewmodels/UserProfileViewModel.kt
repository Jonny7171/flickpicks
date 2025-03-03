package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.UserProfileFirestoreDatabase
import com.example.flickpicks.data.repository.UserProfileRepository
import kotlinx.coroutines.launch

class UserProfileViewModel: ViewModel() {
    val repository = UserProfileRepository(UserProfileFirestoreDatabase())
    fun addUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.addUserProfile(profile)
        }
    }
    fun getUserProfile(profileId: Int) {
        viewModelScope.launch {
            repository.getUserProfile(profileId)
        }
    }
    fun deleteUserProfile(profileId: UserProfile) {
        viewModelScope.launch {
            repository.deleteUserProfile(profileId.id)
        }
    }
    fun updateUserProfile(profile: UserProfile, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updateUserProfile(profile, updates)
        }
    }
}


