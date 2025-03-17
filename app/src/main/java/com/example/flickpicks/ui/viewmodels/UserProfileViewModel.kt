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

    fun acceptFriendRequest(requestUserId: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            // Remove the request from incomingRequests
            current.incomingRequests.remove(requestUserId)
            // Fetch the other user's profile
            val otherUser = repository.getUserProfile(requestUserId) ?: return@launch
            // Remove current user's ID from their outgoingRequests
            otherUser.outgoingRequests.remove(current.id)
            // Add each user to the other's friends list (here we assume "followers" serves as friends)
            current.followers.add(Pair(requestUserId, otherUser.userName))
            otherUser.followers.add(Pair(current.id, current.userName))
            // Update Firestore for both users
            repository.updateUserProfile(current.id, mapOf(
                "incomingRequests" to current.incomingRequests,
                "followers" to current.followers
            ))
            repository.updateUserProfile(otherUser.id, mapOf(
                "outgoingRequests" to otherUser.outgoingRequests,
                "followers" to otherUser.followers
            ))
            _userProfile.value = current
        }
    }

    fun declineFriendRequest(requestUserId: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            current.incomingRequests.remove(requestUserId)
            val otherUser = repository.getUserProfile(requestUserId) ?: return@launch
            otherUser.outgoingRequests.remove(current.id)
            repository.updateUserProfile(current.id, mapOf(
                "incomingRequests" to current.incomingRequests
            ))
            repository.updateUserProfile(otherUser.id, mapOf(
                "outgoingRequests" to otherUser.outgoingRequests
            ))
            _userProfile.value = current
        }
    }

    // For preview purposes: expose a setter for _userProfile
    fun setPreviewProfile(profile: UserProfile) {
        _userProfile.value = profile
    }
}




