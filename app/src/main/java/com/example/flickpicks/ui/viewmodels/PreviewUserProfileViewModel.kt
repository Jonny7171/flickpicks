package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.flickpicks.data.model.Friend
import com.example.flickpicks.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewUserProfileViewModel @Inject constructor() : ViewModel() {

    private val _userProfile = mutableStateOf<UserProfile?>(null)
    val userProfile: State<UserProfile?> = _userProfile

    init {
        // Provide dummy data for preview
        _userProfile.value = UserProfile(
            id = "currentUserId",
            userName = "currentUser",
            followers = mutableListOf("friend1", "friend2"),
            incomingRequests = mutableListOf("request1", "request2")
        )
    }

    fun acceptFriendRequest(requestUserId: String) {
        val current = _userProfile.value ?: return
        // Create a new list for incomingRequests
        val newRequests = current.incomingRequests.toMutableList()
        newRequests.remove(requestUserId)
        // Update the local user profile copy
        _userProfile.value = current.copy(incomingRequests = newRequests)
    }

    fun declineFriendRequest(requestUserId: String) {
        val current = _userProfile.value ?: return
        val newRequests = current.incomingRequests.toMutableList()
        newRequests.remove(requestUserId)
        _userProfile.value = current.copy(incomingRequests = newRequests)
    }
}

