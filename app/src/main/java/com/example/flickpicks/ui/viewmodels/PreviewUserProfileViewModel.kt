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
            followers = mutableListOf(
                Friend("friend1", "Alice"),
                Friend("friend2", "Bob")
            ),
                    incomingRequests = mutableListOf("request1", "request2")
        )
    }

    fun acceptFriendRequest(requestUserId: String) {
        _userProfile.value?.incomingRequests?.remove(requestUserId)
    }

    fun declineFriendRequest(requestUserId: String) {
        _userProfile.value?.incomingRequests?.remove(requestUserId)
    }
}
