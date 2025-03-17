package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.flickpicks.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class UserSearchViewModel : ViewModel() {
    // Holds the list of users matching the query
    val userList = mutableStateOf<List<UserProfile>>(emptyList())

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            userList.value = emptyList()
            return
        }
        val firestore = FirebaseFirestore.getInstance()
        // Query on usernames
        firestore.collection("users")
            .orderBy("userName")
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

    fun sendFriendRequest(targetUser: UserProfile, onComplete: (Boolean, String) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            onComplete(false, "User not authenticated")
            return
        }
        if (currentUserId == targetUser.id) {
            onComplete(false, "Can't send friend request to yourself")
            return
        }
        val firestore = FirebaseFirestore.getInstance()
        val currentUserRef = firestore.collection("users").document(currentUserId)
        val targetUserRef = firestore.collection("users").document(targetUser.id)

        // Ensure request hasn been sent before
        currentUserRef.get().addOnSuccessListener { currentDoc ->
            val outgoing = currentDoc.get("outgoingRequests") as? List<String> ?: emptyList()
            if (outgoing.contains(targetUser.id)) {
                onComplete(false, "Friend request already sent to ${targetUser.userName}")
                return@addOnSuccessListener
            }
            // update the outgoingRequests and incomingRequests field
            currentUserRef.update("outgoingRequests", FieldValue.arrayUnion(targetUser.id))
                .addOnSuccessListener {
                    targetUserRef.update("incomingRequests", FieldValue.arrayUnion(currentUserId))
                        .addOnSuccessListener {
                            onComplete(true, "Friend request sent to ${targetUser.userName}!")
                        }
                        .addOnFailureListener {
                            onComplete(false, "Failed to update target user's incoming requests")
                        }
                }
                .addOnFailureListener {
                    onComplete(false, "Failed to update your outgoing requests")
                }
        }.addOnFailureListener {
            onComplete(false, "Failed to get your user data")
        }
    }
}
