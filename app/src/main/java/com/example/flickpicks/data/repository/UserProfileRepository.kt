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
    suspend fun get(profileId: Int): UserProfile?
    suspend fun delete(profileId: Int): Boolean
    suspend fun update(profile: UserProfile, updates: Map<String, Any>): Boolean
}

class UserProfileInMemoryDatabase : UserProfileDatabase {
    private val profiles = mutableMapOf<String, UserProfile>()

    override suspend fun add(profile: UserProfile): Boolean {
        profiles[profile.id.toString()] = profile
        return true
    }

    override suspend fun get(profileId: Int): UserProfile? {
        return profiles[profileId.toString()]
    }

    override suspend fun delete(profileId: Int): Boolean {
        return profiles.remove(profileId.toString()) != null
    }

    override suspend fun update(profile: UserProfile, updates: Map<String, Any>): Boolean {
        val existingProfile = profiles[profile.id.toString()] ?: return false
        updates.forEach { (key, value) ->
            when (key) {
                "name" -> existingProfile.name = value as String
                "profilePicUrl" -> existingProfile.profilePicUrl = value as String
                "username" -> existingProfile.userName = value as String
                "password" -> existingProfile.password = value as String
                "email" -> existingProfile.email = value as String
                "phoneNumber" -> existingProfile.phoneNumber = value as String
                "following" -> existingProfile.following = value as MutableList<Pair<String, String>>
                "followers" -> existingProfile.followers = value as MutableList<Pair<String, String>>
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
    override suspend fun get(profileId: Int): UserProfile? {
        return try {
            val document = db.collection("users").document(profileId.toString()).get().await()
            val review = document.toObject(UserProfile::class.java)
            Log.d("Firestore data", "Profile found $review ")
            review
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting profile", e)
            null
        }
    }

    // Update a UserProfile
    override suspend fun update(profile: UserProfile, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("users").document(profile.id.toString()).update(updates).await()
            Log.d("Firestore", "Profile updated for: $profile.id")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating review", e)
            false
        }
    }

    // Delete a UserProfile
    override suspend fun delete(profileId: Int): Boolean {
        return try {
            db.collection("users").document(profileId.toString()).delete().await()
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

    suspend fun getUserProfile(profileId: Int): UserProfile? {
        return db.get(profileId)
    }

    suspend fun deleteUserProfile(profileId: Int): Boolean {
        return db.delete(profileId)
    }

    suspend fun updateUserProfile(profile: UserProfile, updates: Map<String, Any>): Boolean {
        return db.update(profile, updates)
    }
}
