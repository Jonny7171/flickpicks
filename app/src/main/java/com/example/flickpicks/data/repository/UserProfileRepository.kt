package com.example.flickpicks.data.repository

import android.util.Log
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.model.UserProfile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface UserProfileDatabase {
    suspend fun add(profile: UserProfile): Boolean
    suspend fun get(profileId: String): UserProfile?
    suspend fun delete(profileId: String): Boolean
    suspend fun update(profileId: String, updates: Map<String, Any>): Boolean
}

class UserProfileInMemoryDatabase : UserProfileDatabase {
    private val profiles = mutableMapOf<String, UserProfile>()

    override suspend fun add(profile: UserProfile): Boolean {
        profiles[profile.id] = profile
        return true
    }

    override suspend fun get(profileId: String): UserProfile? {
        return profiles[profileId]
    }

    override suspend fun delete(profileId: String): Boolean {
        return profiles.remove(profileId) != null
    }

    override suspend fun update(profileId: String, updates: Map<String, Any>): Boolean {
        val existingProfile = profiles[profileId] ?: return false
        updates.forEach { (key, value) ->
            when (key) {
                "name" -> existingProfile.name = value as String
                "profilePicUrl" -> existingProfile.profilePicUrl = value as String
                "username" -> existingProfile.userName = value as String
                "password" -> existingProfile.password = value as String
                "email" -> existingProfile.email = value as String
                "phoneNumber" -> existingProfile.phoneNumber = value as String
                "following" -> existingProfile.following = value as MutableList<String>
                "followers" -> existingProfile.followers = value as MutableList<String>
                "blockedUsers" -> existingProfile.blockedUsers = value as MutableList<String>
                "incomingRequests" -> existingProfile.incomingRequests = value as MutableList<String>
                "outgoingRequests" -> existingProfile.outgoingRequests = value as MutableList<String>
                "moviesSaved" -> existingProfile.moviesSaved = value as MutableList<String>
                "moviesReviewed" -> existingProfile.moviesReviewed = value as MutableList<MovieReview>
                "moviesLiked" -> existingProfile.moviesLiked = value as MutableList<String>
                "moviesDisliked" -> existingProfile.moviesDisliked = value as MutableList<String>
                "genrePreferences" -> existingProfile.genrePreferences = value as MutableList<String>
                "partyGroups" -> existingProfile.partyGroups = value as MutableList<PartyGroup>
            }
        }
        return true
    }
}


class UserProfileFirestoreDatabase : UserProfileDatabase {

    private val db = Firebase.firestore

    // Add a UserProfile
    override suspend fun add(profile: UserProfile): Boolean {
        return try {
            db.collection("users").document(profile.id.toString()).set(profile).await()
            Log.d("Firestore", "Profile added for: ${profile.name}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding profile", e)
            false
        }
    }

    // Get a UserProfile
    override suspend fun get(profileId: String): UserProfile? {
        return try {
            val document = db.collection("users").document(profileId).get().await()
            val review = document.toObject(UserProfile::class.java)
            Log.d("Firestore data", "Profile found $review ")
            review
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting profile", e)
            null
        }
    }

    // Update a UserProfile
    override suspend fun update(profileId: String, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("users").document(profileId).update(updates).await()
            Log.d("Firestore", "Profile updated for: $profileId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating review", e)
            false
        }
    }

    // Delete a UserProfile
    override suspend fun delete(profileId: String): Boolean {
        return try {
            db.collection("users").document(profileId).delete().await()
            Log.d("Firestore", "Profile deleted for: $profileId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting profile", e)
            false
        }
    }
}

@Singleton
class UserProfileRepository @Inject constructor(private val db: UserProfileDatabase) {

    suspend fun addUserProfile(userProfile: UserProfile): Boolean {
        return db.add(userProfile)
    }

    suspend fun getUserProfile(profileId: String): UserProfile? {
        return db.get(profileId)
    }

    suspend fun deleteUserProfile(profileId: String): Boolean {
        return db.delete(profileId)
    }

    suspend fun updateUserProfile(profileId:String, updates: Map<String, Any>): Boolean {
        return db.update(profileId, updates)
    }
}
