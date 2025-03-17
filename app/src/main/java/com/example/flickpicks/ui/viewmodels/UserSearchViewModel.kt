package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.flickpicks.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserSearchViewModel : ViewModel() {
    // Holds the list of users matching the search query.
    val userList = mutableStateOf<List<UserProfile>>(emptyList())

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            userList.value = emptyList()
            return
        }
        val firestore = FirebaseFirestore.getInstance()
        // Get Users
        firestore.collection("users")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull {
                    it.toObject(UserProfile::class.java)
                }
                userList.value = users
            }
            .addOnFailureListener {
                userList.value = emptyList()
            }
    }
//Send friend requests, ie updating Outgoing and Incoming requests appropriately
    fun sendFriendRequest(targetUser: UserProfile, onComplete: (Boolean) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            onComplete(false)
            return
        }
        val firestore = FirebaseFirestore.getInstance()
        val currentUserRef = firestore.collection("users").document(currentUserId)
        val targetUserRef = firestore.collection("users").document(targetUser.id)

        // Update current user's outgoingRequests
        currentUserRef.update("outgoingRequests", FieldValue.arrayUnion(targetUser.id))
            .addOnSuccessListener {
                // Update target user's incomingRequests
                targetUserRef.update("incomingRequests", FieldValue.arrayUnion(currentUserId))
                    .addOnSuccessListener {
                        onComplete(true)
                    }
                    .addOnFailureListener { onComplete(false) }
            }
            .addOnFailureListener { onComplete(false) }
    }
}
