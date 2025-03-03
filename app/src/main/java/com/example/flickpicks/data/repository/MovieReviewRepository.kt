package com.example.flickpicks.data.repository

import android.util.Log
import com.example.flickpicks.data.model.MovieReview
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface MovieReviewDatabase {
    suspend fun add(movieReview: MovieReview): Boolean
    suspend fun get(reviewId: Int): MovieReview?
    suspend fun delete(reviewId: Int): Boolean
    suspend fun update(movieReview: MovieReview, updates: Map<String, Any>): Boolean
}

class MovieReviewInMemoryDatabase : MovieReviewDatabase {
    private val reviews = mutableMapOf<String, MovieReview>()

    override suspend fun add(movieReview: MovieReview): Boolean {
        reviews[movieReview.id.toString()] = movieReview
        return true
    }

    override suspend fun get(reviewId: Int): MovieReview? {
        return reviews[reviewId.toString()]
    }

    override suspend fun delete(reviewId: Int): Boolean {
        return reviews.remove(reviewId.toString()) != null
    }

    override suspend fun update(movieReview: MovieReview, updates: Map<String, Any>): Boolean {
        val existingReview = reviews[movieReview.id.toString()] ?: return false
        updates.forEach { (key, value) ->
            when (key) {
                "movieTitle" -> existingReview.movieTitle = value as String
                "release_date" -> existingReview.release_date = value as String
                "tagline" -> existingReview.tagline = value as String
                "overview" -> existingReview.overview = value as String
                "genres" -> existingReview.genres = value as List<String>
                "reviewerName" -> existingReview.reviewerName = value as String
                "reviewText" -> existingReview.reviewText = value as String
                "rating" -> existingReview.rating = value as Int
                "streamingPlatform" -> existingReview.streamingPlatform = value as String
            }
        }
        return true
    }
}

class MovieReviewFirestoreDatabase : MovieReviewDatabase {

    private val db = Firebase.firestore

    // Add a MovieReview
    override suspend fun add(movieReview: MovieReview): Boolean {
        return try {
            db.collection("movie_reviews").document(movieReview.id.toString()).set(movieReview).await()
            Log.d("Firestore", "Review added for: ${movieReview.movieTitle}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding review", e)
            false
        }
    }

    // Get a MovieReview
    override suspend fun get(reviewId: Int): MovieReview? {
        return try {
            val document = db.collection("movie_reviews").document(reviewId.toString()).get().await()
            val review = document.toObject(MovieReview::class.java)
            Log.d("Firestore data", "Review found $review ")
            review
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting review", e)
            null
        }
    }

    // Update a MovieReview
    override suspend fun update(movieReview: MovieReview, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("movie_reviews").document(movieReview.id.toString()).update(updates).await()
            Log.d("Firestore", "Review updated for: $movieReview.id")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating review", e)
            false
        }
    }

    // Delete a MovieReview
    override suspend fun delete(reviewId: Int): Boolean {
        return try {
            db.collection("movie_reviews").document(reviewId.toString()).delete().await()
            Log.d("Firestore", "Review deleted for: $reviewId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting review", e)
            false
        }
    }
}



class MovieReviewRepository(private val db: MovieReviewDatabase) {

    suspend fun addMovieReview(movieReview: MovieReview): Boolean {
        return db.add(movieReview)
    }

    suspend fun getMovieReview(reviewId: Int): MovieReview? {
        return db.get(reviewId)
    }

    suspend fun deleteMovieReview(reviewId: Int): Boolean {
        return db.delete(reviewId)
    }

    suspend fun updateMovieReview(movieReview: MovieReview, updates: Map<String, Any>): Boolean {
        return db.update(movieReview, updates)
    }
}
