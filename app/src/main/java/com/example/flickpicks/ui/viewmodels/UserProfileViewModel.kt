package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.UserProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: UserProfileRepository,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _userProfile = mutableStateOf<UserProfile?>(null)
    val userProfile: State<UserProfile?> = _userProfile

    fun addUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.addUserProfile(profile)
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
            val newIncoming = current.incomingRequests.toMutableList()
            newIncoming.remove(requestUserId)

            val otherUser = repository.getUserProfile(requestUserId) ?: return@launch
            val newOutgoing = otherUser.outgoingRequests.toMutableList()
            newOutgoing.remove(current.id)

            val newFollowersForCurrent = current.followers.toMutableList()
            newFollowersForCurrent.add(requestUserId)

            val newFollowersForOther = otherUser.followers.toMutableList()
            newFollowersForOther.add(current.id)

            repository.updateUserProfile(current.id, mapOf(
                "incomingRequests" to newIncoming,
                "followers" to newFollowersForCurrent
            ))
            repository.updateUserProfile(otherUser.id, mapOf(
                "outgoingRequests" to newOutgoing,
                "followers" to newFollowersForOther
            ))

            _userProfile.value = current.copy(
                incomingRequests = newIncoming,
                followers = newFollowersForCurrent
            )
        }
    }

    fun declineFriendRequest(requestUserId: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val newIncoming = current.incomingRequests.toMutableList()
            newIncoming.remove(requestUserId)

            val otherUser = repository.getUserProfile(requestUserId) ?: return@launch
            val newOutgoing = otherUser.outgoingRequests.toMutableList()
            newOutgoing.remove(current.id)

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
                .filterNot { it == friendId }
                .toMutableList()

            val friendProfile = repository.getUserProfile(friendId) ?: return@launch
            val newFriendFollowers = friendProfile.followers
                .filterNot { it == current.id }
                .toMutableList()

            repository.updateUserProfile(current.id, mapOf("followers" to newFollowers))
            repository.updateUserProfile(friendId, mapOf("followers" to newFriendFollowers))

            _userProfile.value = current.copy(
                followers = newFollowers
            )
        }
    }

    fun removeSavedMovie(movie: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val updatedMoviesSaved = current.moviesSaved.toMutableList()
            updatedMoviesSaved.remove(movie)
            repository.updateUserProfile(current.id, mapOf("moviesSaved" to updatedMoviesSaved))
            _userProfile.value = current.copy(moviesSaved = updatedMoviesSaved)
        }
    }


    fun removeLikedMovie(movie: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val updatedMoviesLiked = current.moviesLiked.toMutableList()
            updatedMoviesLiked.remove(movie)
            repository.updateUserProfile(current.id, mapOf("moviesLiked" to updatedMoviesLiked))
            _userProfile.value = current.copy(moviesLiked = updatedMoviesLiked)
        }
    }


    fun removeDislikedMovie(movie: String) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val updatedMoviesDisliked = current.moviesDisliked.toMutableList()
            updatedMoviesDisliked.remove(movie)
            repository.updateUserProfile(current.id, mapOf("moviesDisliked" to updatedMoviesDisliked))
            _userProfile.value = current.copy(moviesDisliked = updatedMoviesDisliked)
        }
    }


    fun removeReview(review: MovieReview) {
        viewModelScope.launch {
            val current = _userProfile.value ?: return@launch
            val updatedMoviesReviewed = current.moviesReviewed.toMutableList()
            updatedMoviesReviewed.remove(review)
            repository.updateUserProfile(
                current.id,
                mapOf("moviesReviewed" to updatedMoviesReviewed)
            )
            _userProfile.value = current.copy(moviesReviewed = updatedMoviesReviewed)
        }
    }

}
