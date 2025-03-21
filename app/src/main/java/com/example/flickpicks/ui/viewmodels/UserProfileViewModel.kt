package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.Friend
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
            // Create new copies of lists to force recomposition:
            val newIncoming = current.incomingRequests.toMutableList().apply {
                remove(requestUserId)
            }
            val otherUser = repository.getUserProfile(requestUserId) ?: return@launch
            val newOutgoing = otherUser.outgoingRequests.toMutableList().apply {
                remove(current.id)
            }
            // Update followers lists immutably:
            val newFollowersForCurrent = current.followers.toMutableList().apply {
                add(Friend(requestUserId, otherUser.userName))
            }
            val newFollowersForOther = otherUser.followers.toMutableList().apply {
                add(Friend(current.id, current.userName))
            }

            // Update Firestore for both users
            repository.updateUserProfile(current.id, mapOf(
                "incomingRequests" to newIncoming,
                "followers" to newFollowersForCurrent
            ))
            repository.updateUserProfile(otherUser.id, mapOf(
                "outgoingRequests" to newOutgoing,
                "followers" to newFollowersForOther
            ))

            // Update local state using a new copy of currentUser
            _userProfile.value = current.copy(
                incomingRequests = newIncoming,
                followers = newFollowersForCurrent
            )
        }
    }

    fun declineFriendRequest(requestUserId: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val newIncoming = current.incomingRequests.toMutableList().apply {
                remove(requestUserId)
            }
            val otherUser = repository.getUserProfile(requestUserId) ?: return@launch
            val newOutgoing = otherUser.outgoingRequests.toMutableList().apply {
                remove(current.id)
            }
            // Update Firestore for both users
            repository.updateUserProfile(current.id, mapOf(
                "incomingRequests" to newIncoming
            ))
            repository.updateUserProfile(otherUser.id, mapOf(
                "outgoingRequests" to newOutgoing
            ))
            _userProfile.value = current.copy(
                incomingRequests = newIncoming
            )
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val newFollowers = current.followers
                .filterNot { it.id == friendId }
                .toMutableList()

            // Get the friend profile
            val friendProfile = repository.getUserProfile(friendId) ?: return@launch
            val newFriendFollowers = friendProfile.followers
                .filterNot { it.id == current.id }
                .toMutableList()

            // Update Firestore for both users
            repository.updateUserProfile(current.id, mapOf("followers" to newFollowers))
            repository.updateUserProfile(friendId, mapOf("followers" to newFriendFollowers))
            _userProfile.value = current.copy(
                followers = newFollowers
            )
        }
    }
    fun setPreviewProfile(profile: UserProfile) {
        _userProfile.value = profile
    }
}




