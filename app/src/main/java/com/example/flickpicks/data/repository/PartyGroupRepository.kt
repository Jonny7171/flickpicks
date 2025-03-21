package com.example.flickpicks.data.repository

import android.util.Log
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.model.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface PartyGroupDatabase {
    suspend fun add(group: PartyGroup, userId: String): Boolean
    suspend fun get(groupId: Int): PartyGroup?
    suspend fun delete(groupId: Int): Boolean
    suspend fun update(group: PartyGroup, updates: Map<String, Any>): Boolean
    suspend fun addChatMessage(groupId: Int, message: ChatMessage): Boolean
    suspend fun getChatMessages(groupId: Int): List<ChatMessage>
    suspend fun getUserPartyGroups(userId: String): List<PartyGroup>
    suspend fun getTotalPartyGroupsCount(): Int
    suspend fun listenForChatMessages(groupId: Int, onMessagesUpdated: (List<ChatMessage>) -> Unit)
}

class PartyGroupInMemoryDatabase : PartyGroupDatabase {
    private val groups = mutableMapOf<String, PartyGroup>()
    private val messageListeners = mutableMapOf<Int, MutableList<(List<ChatMessage>) -> Unit>>()
    private fun notifyMessageListeners(groupId: Int) {
        val messages = groups[groupId.toString()]?.chatMessages?.sortedBy { it.timestamp } ?: emptyList()
        messageListeners[groupId]?.forEach { it(messages) }
    }
    override suspend fun add(group: PartyGroup, userId: String): Boolean {
        val maxId = groups.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0

        // Assign the next ID dynamically
        val newGroup = group.copy(id = maxId + 1)

        // Store in memory
        groups[newGroup.id.toString()] = newGroup

        Log.d("InMemoryDB", "Added new group with ID: ${newGroup.id}")
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
                "name" -> group.groupName = value as String
                "members" -> group.members = value as MutableList<String>
                "timesAvailable" -> group.timesAvailable =
                    value as MutableMap<String, Map<String, List<String>>>
                "winnerMovie" -> group.winnerMovie = value as Movie
                "pastWatchedMovies" -> group.pastWatchedMovies = value as MutableList<String>
                "chatMessages" -> group.chatMessages = value as MutableList<ChatMessage>
            }
        }
        return true
    }
    override suspend fun listenForChatMessages(groupId: Int, onMessagesUpdated: (List<ChatMessage>) -> Unit) {
        // Ensure there’s a list of listeners for this group
        if (!messageListeners.containsKey(groupId)) {
            messageListeners[groupId] = mutableListOf()
        }
        messageListeners[groupId]?.add(onMessagesUpdated)

        // Immediately send the current messages to the observer
        val messages = groups[groupId.toString()]?.chatMessages?.sortedBy { it.timestamp } ?: emptyList()
        onMessagesUpdated(messages)
    }

    override suspend fun addChatMessage(groupId: Int, message: ChatMessage): Boolean {
        val group = groups[groupId.toString()] ?: return false

        group.chatMessages.add(message)
        return true
    }

    // New function to get chat messages
    override suspend fun getChatMessages(groupId: Int): List<ChatMessage> {
        val group = groups[groupId.toString()] ?: return emptyList()
        return group.chatMessages ?: emptyList()
    }

    override suspend fun getUserPartyGroups(userId: String): List<PartyGroup> {
        return groups.values.filter { group ->
            group.members.contains(userId) // Filter groups where user is a member
        }
    }

    override suspend fun getTotalPartyGroupsCount(): Int {
        return groups.size // Return total number of groups in memory
    }
}


class PartyGroupFirestoreDatabase : PartyGroupDatabase {

    private val db = Firebase.firestore

    // Add a PartyGroup
    override suspend fun add(group: PartyGroup, userId: String): Boolean {
        return try {
            val groupsSnapshot = db.collection("party_groups").get().await()
            val maxId = groupsSnapshot.documents
                .mapNotNull { it.getLong("id")?.toInt() }
                .maxOrNull() ?: 0 // Default to 0 if no groups exist

            val newGroup = group.copy(id = maxId + 1)
            // add group to firestore
            db.collection("party_groups").document(newGroup.id.toString()).set(newGroup).await()

            // update's user profile to include group
            val currUser = db.collection("users").document(userId)
            currUser.update("partyGroups", FieldValue.arrayUnion(newGroup)).await()

            Log.d("Firestore", "Party group added for: ${newGroup.groupName}")
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

        /*
        return try {
            db.collection("users").document(groupId.toString()).delete().await()
            db.collection("party_groups").document(groupId.toString()).delete().await()

            Log.d("Firestore", "Party group deleted for: $groupId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting party group", e)
            false
        }

         */
        return try {
            val batch = db.batch()

            // Get all users
            val usersSnapshot = db.collection("users").get().await()
            for (userDoc in usersSnapshot.documents) {
                val userRef = userDoc.reference
                batch.update(userRef, "partyGroups", FieldValue.arrayRemove(groupId))
            }

            // Delete group from party_groups
            batch.delete(db.collection("party_groups").document(groupId.toString()))

            batch.commit().await()

            Log.d("Firestore", "Successfully deleted group $groupId from all users and party_groups")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting party group $groupId", e)
            false
        }



    }
    override suspend fun listenForChatMessages(groupId: Int, onMessagesUpdated: (List<ChatMessage>) -> Unit) {
        db.collection("party_groups")
            .document(groupId.toString())
            .collection("messages")
            .orderBy("timestamp") // Ensure messages are sorted chronologically
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Error listening for chat messages", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.toObjects(ChatMessage::class.java)
                    onMessagesUpdated(messages)
                }
            }
    }

    override suspend fun addChatMessage(groupId: Int, message: ChatMessage): Boolean {
        return try {
            val messageWithTimestamp = message.copy(timestamp = System.currentTimeMillis()) // Add timestamp
            db.collection("party_groups")
                .document(groupId.toString())
                .collection("messages")
                .add(messageWithTimestamp)
                .await()
            Log.d("Firestore", "Added chat message")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding chat message", e)
            false
        }
    }

    override suspend fun getChatMessages(groupId: Int): List<ChatMessage> {
        return try {
            val snapshot = db.collection("party_groups")
                .document(groupId.toString())
                .collection("messages")
                .get()
                .await()
            snapshot.toObjects(ChatMessage::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting chat messages", e)
            emptyList()

        }
    }

    override suspend fun getUserPartyGroups(userId: String): List<PartyGroup> {
        return try {
            val groupsSnapshot = db.collection("party_groups").get().await()
            val allGroups = groupsSnapshot.toObjects(PartyGroup::class.java)

            // Filter only groups where the userId is in the members list
            val userGroups = allGroups.filter { it.members.contains(userId) }

            Log.d("Firestore", "Fetched groups for user $userId: ${userGroups.joinToString { it.groupName }}")
            userGroups
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching user party groups", e)
            emptyList()
        }
    }

    override suspend fun getTotalPartyGroupsCount(): Int {
        return try {
            val snapshot = db.collection("party_groups").get().await()
            snapshot.size()
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching total party groups", e)
            0
        }
    }


}

class PartyGroupRepository @Inject constructor(private val db: PartyGroupDatabase) {

    suspend fun addPartyGroup(group: PartyGroup, userId: String): Boolean {
        return db.add(group, userId)
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

    suspend fun sendChatMessage(groupId: Int, message: ChatMessage): Boolean {
        return db.addChatMessage(groupId, message)
    }

    suspend fun getChatMessages(groupId: Int): List<ChatMessage> {
        return db.getChatMessages(groupId)
    }

    suspend fun getUserPartyGroups(userId: String): List<PartyGroup> {
        return db.getUserPartyGroups(userId)
    }

    suspend fun getTotalPartyGroupsCount(): Int {
        return db.getTotalPartyGroupsCount()
    }

    suspend fun listenForChatMessages(groupId: Int, onMessagesUpdated: (List<ChatMessage>) -> Unit) {
        db.listenForChatMessages(groupId, onMessagesUpdated)
    }
}
