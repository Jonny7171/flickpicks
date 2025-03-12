
package com.example.flickpicks.data.repository

import com.example.flickpicks.data.model.Genre
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface GenreDatabase {
    suspend fun add(genre: Genre): Boolean
    suspend fun get(genreName: String): Genre?
    suspend fun delete(genreName: String): Boolean
    suspend fun update(genre: Genre, updates: Map<String, Any>): Boolean
}

class GenreInMemoryDatabase : GenreDatabase {
    private val genres = mutableMapOf<String, Genre>()

    override suspend fun add(genre: Genre): Boolean {
        genres[genre.name] = genre
        return true
    }

    override suspend fun get(genreName: String): Genre? {
        return genres[genreName]
    }

    override suspend fun delete(genreName: String): Boolean {
        return genres.remove(genreName) != null
    }

    override suspend fun update(genre: Genre, updates: Map<String, Any>): Boolean {
        val genre = genres[genre.name] ?: return false
        updates.forEach { (key, value) ->
            when (key) {
                "name" -> genre.name = value as String
                "count" -> genre.count = value as Int
            }
        }
        return true
    }
}


class GenreFirestoreDatabase : GenreDatabase {

    private val db = Firebase.firestore

    override suspend fun add(genre: Genre): Boolean {
        return try {
            db.collection("genres").document(genre.name).set(genre).await()
            Log.d("Firestore", "Genre added: ${genre.name}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding genre", e)
            false
        }
    }

    override suspend fun get(genreName: String): Genre? {
        return try {
            val document = db.collection("genres").document(genreName).get().await()
            val genre = document.toObject(Genre::class.java)
            Log.d("Firestore data", "Genre found $genre ")
            genre

        } catch (e: Exception) {
            Log.e("Firestore", "Error getting genre", e)
            null
        }
    }

    override suspend fun delete(genreName: String): Boolean {
        return try {
            db.collection("genres").document(genreName).delete().await()
            Log.d("Firestore", "Genre deleted: $genreName")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting genre", e)
            false
        }
    }
    override suspend fun update(genre: Genre, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("genres").document(genre.name).set(genre).await()
                db.collection("genres").document(genre.name).update(updates).await()
                Log.d("Firestore", "Genre updated: $genre.name")
                true
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating genre", e)
            false
        }
    }
}

class GenreRepository @Inject constructor(private val db: GenreDatabase) {

    suspend fun addGenre(genre: Genre): Boolean {
        return db.add(genre)
    }

    suspend fun getGenre(genreName: String): Genre? {
        return db.get(genreName)
    }

    suspend fun deleteGenre(genreName: String): Boolean {
        return db.delete(genreName)
    }

    suspend fun updateGenre(genre: Genre, updates: Map<String, Any>): Boolean {
        return db.update(genre, updates)
    }
}

