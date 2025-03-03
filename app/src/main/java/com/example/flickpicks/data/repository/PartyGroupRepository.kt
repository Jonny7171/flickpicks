package com.example.flickpicks.data.repository

import android.util.Log
import com.example.flickpicks.data.model.PartyGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface PartyGroupDatabase {
    suspend fun add(group: PartyGroup): Boolean
    suspend fun get(groupId: Int): PartyGroup?
    suspend fun delete(groupId: Int): Boolean
    suspend fun update(group: PartyGroup, updates: Map<String, Any>): Boolean
}

class PartyGroupInMemoryDatabase : PartyGroupDatabase {
    private val groups = mutableMapOf<String, PartyGroup>()

    override suspend fun add(group: PartyGroup): Boolean {
        groups[group.id.toString()] = group
        return true
    }

    override suspend fun get(groupId: Int): PartyGroup? {
        return groups[groupId.toString()]
    }

    override suspend fun delete(groupId: Int): Boolean {
        return groups.remove(groupId.toString()) != null
    }

    override suspend fun update(group: PartyGroup, updates: Map<String, Any>): Boolean {
        val group = groups[group.id.toString()] ?: return false
        updates.forEach { (key, value) ->
            when (key) {
                "id" -> group.id = value as Int
                "name" -> group.name = value as String
            }
        }
        return true
    }
}


class PartyGroupFirestoreDatabase : PartyGroupDatabase {

    private val db = Firebase.firestore

    // Add a PartyGroup
    override suspend fun add(group: PartyGroup): Boolean {
        return try {
            db.collection("party_groups").document(group.id.toString()).set(group).await()
            Log.d("Firestore", "Party group added for: ${group.name}")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding party group", e)
            false
        }
    }

    // Get a PartyGroup
    override suspend fun get(groupId: Int): PartyGroup? {
        return try {
            val document = db.collection("party_groups").document(groupId.toString()).get().await()
            val group = document.toObject(PartyGroup::class.java)
            Log.d("Firestore data", "Part group found $group ")
            group
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting party group", e)
            null
        }
    }

    // Update a PartyGroup
    override suspend fun update(group: PartyGroup, updates: Map<String, Any>): Boolean {
        return try {
            db.collection("party_groups").document(group.id.toString()).update(updates).await()
            Log.d("Firestore", "Party group updated for: $group.id")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating party group", e)
            false
        }
    }

    // Delete a PartyGroup
    override suspend fun delete(groupId: Int): Boolean {
        return try {
            db.collection("users").document(groupId.toString()).delete().await()
            Log.d("Firestore", "Party group deleted for: $groupId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting party group", e)
            false
        }
    }
}

class PartyGroupRepository(private val db: PartyGroupDatabase) {

    suspend fun addPartyGroup(group: PartyGroup): Boolean {
        return db.add(group)
    }

    suspend fun getPartyGroup(groupId: Int): PartyGroup? {
        return db.get(groupId)
    }

    suspend fun deletePartyGroup(groupId: Int): Boolean {
        return db.delete(groupId)
    }

    suspend fun updatePartyGroup(group: PartyGroup, updates: Map<String, Any>): Boolean {
        return db.update(group, updates)
    }
}
