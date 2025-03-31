package com.example.flickpicks.data.repository

import android.util.Log
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.data.model.GENRE_MAP
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.model.VoteCounts
import com.google.firebase.firestore.FieldValue
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
    suspend fun voteOnMovie(id: Int, userId: String, movieId: String, vote: Boolean): Boolean
}

class PartyGroupInMemoryDatabase : PartyGroupDatabase {
    private val groups = mutableMapOf<String, PartyGroup>()
    private val messageListeners = mutableMapOf<Int, MutableList<(List<ChatMessage>) -> Unit>>()
    private fun notifyMessageListeners(groupId: Int) {
        val messages =
            groups[groupId.toString()]?.chatMessages?.sortedBy { it.timestamp } ?: emptyList()
        messageListeners[groupId]?.forEach { it(messages) }
    }

    override suspend fun add(group: PartyGroup, userId: String): Boolean {
        val maxId = groups.keys.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0

        // Assign the next ID dynamically
        val newGroup = group.copy(id = maxId + 1)

        // Store in memory
        groups[newGroup.id.toString()] = newGroup

        //Log.d("InMemoryDB", "Added new group with ID: ${newGroup.id}")
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
                "genreMovieSuggestions" -> group.genreMovieSuggestions as MutableList<Movie>
                "movieVotes" -> group.movieVotes = value as MutableMap<String, VoteCounts>
                "usersVoted" -> group.usersVoted = value as MutableMap<String, MutableList<String>>
                "gameActive" -> group.gameActive = value as Boolean

            }
        }
        return true
    }

    override suspend fun listenForChatMessages(
        groupId: Int,
        onMessagesUpdated: (List<ChatMessage>) -> Unit
    ) {
        // Ensure there’s a list of listeners for this group
        if (!messageListeners.containsKey(groupId)) {
            messageListeners[groupId] = mutableListOf()
        }
        messageListeners[groupId]?.add(onMessagesUpdated)

        // Immediately send the current messages to the observer
        val messages =
            groups[groupId.toString()]?.chatMessages?.sortedBy { it.timestamp } ?: emptyList()
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

    override suspend fun voteOnMovie(
        id: Int,
        userId: String,
        movieId: String,
        vote: Boolean
    ): Boolean {
        val group = get(id) ?: return false

        if (!group.gameActive) {
            //Log.w("Vote", "Game is not active for group $id")
            return false
        }

        val voteCounts = group.movieVotes.getOrPut(movieId) { VoteCounts() }
        val userVotedMovies = group.usersVoted.getOrPut(userId) { mutableListOf() }

        if (userVotedMovies.contains(movieId)) {
            //Log.d("Vote", "User $userId already voted for movie $movieId")
            return false
        }

        if (vote) {
            voteCounts.yes += 1
        } else {
            voteCounts.no += 1
        }

        userVotedMovies.add(movieId)
        group.usersVoted[userId] = userVotedMovies
        group.movieVotes[movieId] = voteCounts

        val totalVotesCast = group.movieVotes.values.sumOf { it.yes + it.no }
        val totalVotesNeeded = group.genreMovieSuggestions.size * group.members.size

        if (totalVotesCast >= totalVotesNeeded) {
            group.gameActive = false
            val topMovieId = group.movieVotes.maxByOrNull { it.value.yes }?.key
            val winner = group.genreMovieSuggestions.find { it.id == topMovieId }
            if (winner != null) {
                group.winnerMovie = winner
                //Log.d("Vote", "Voting complete — Winner is ${winner.title}")
            } else {
                //Log.d("Vote", "Voting complete — but no winner found")
            }
        }

        return update(
            group,
            mapOf(
                "movieVotes" to group.movieVotes,
                "usersVoted" to group.usersVoted,
                "winnerMovie" to group.winnerMovie,
                "gameActive" to group.gameActive
            )
        )
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
                val document =
                    db.collection("party_groups").document(groupId.toString()).get().await()
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

                Log.d(
                    "Firestore",
                    "Successfully deleted group $groupId from all users and party_groups"
                )
                true
            } catch (e: Exception) {
                Log.e("Firestore", "Error deleting party group $groupId", e)
                false
            }


        }

        override suspend fun listenForChatMessages(
            groupId: Int,
            onMessagesUpdated: (List<ChatMessage>) -> Unit
        ) {
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
                val messageWithTimestamp =
                    message.copy(timestamp = System.currentTimeMillis()) // Add timestamp
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

                Log.d(
                    "Firestore",
                    "Fetched groups for user $userId: ${userGroups.joinToString { it.groupName }}"
                )
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

        override suspend fun voteOnMovie(
            id: Int,
            userId: String,
            movieId: String,
            vote: Boolean
        ): Boolean {
            val group = get(id) ?: return false

            val userVotedMovies = group.usersVoted.getOrPut(userId) { mutableListOf() }
            if (userVotedMovies.contains(movieId)) return false // Prevent double voting

            // Update vote counts
            val currentVoteCount = group.movieVotes.getOrDefault(movieId, VoteCounts())
            group.movieVotes[movieId] = if (vote) {
                currentVoteCount.copy(yes = currentVoteCount.yes + 1)
            } else {
                currentVoteCount.copy(no = currentVoteCount.no + 1)
            }

            userVotedMovies.add(movieId)
            group.usersVoted[userId] = userVotedMovies

            val totalVotesCast = group.movieVotes.values.sumOf { it.yes + it.no }
            val totalVotesNeeded = group.genreMovieSuggestions.size * group.members.size

            if (totalVotesCast >= totalVotesNeeded) {
                group.gameActive = false
                val topMovieId = group.movieVotes.maxByOrNull { it.value.yes }?.key
                val winner = group.genreMovieSuggestions.find { it.id == topMovieId }
                if (winner != null) {
                    group.winnerMovie = winner
                }
            }

            return update(
                group, mapOf(
                    "movieVotes" to group.movieVotes,
                    "usersVoted" to group.usersVoted,
                    "winnerMovie" to group.winnerMovie,
                ))
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

        suspend fun listenForChatMessages(
            groupId: Int,
            onMessagesUpdated: (List<ChatMessage>) -> Unit
        ) {
            db.listenForChatMessages(groupId, onMessagesUpdated)
        }

        suspend fun voteOnMovie(id: Int, userId: String, movieId: String, vote: Boolean): Boolean {
            return db.voteOnMovie(id, userId, movieId, vote)
        }
        suspend fun startNewGame(
            id: Int,
            getUserProfile: suspend (String) -> UserProfile?,
            fetchMoviesByGenre: suspend (List<String>) -> List<Movie>
        ): Boolean {
            val group = getPartyGroup(id) ?: return false

            val memberGenres = group.members.mapNotNull { memberId ->
                getUserProfile(memberId)?.genrePreferences?.toSet()
            }

            val commonGenres = memberGenres.reduceOrNull { acc, set -> acc.intersect(set) }?.toList() ?: emptyList()
            val fallbackGenres = listOf(
                "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary",
                "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Mystery",
                "Romance", "Science Fiction", "TV Movie", "Thriller", "War", "Western"
            )

            val genreToUse = if (commonGenres.isNotEmpty()) commonGenres.random() else fallbackGenres.random()
            val genreId = GENRE_MAP[genreToUse.lowercase()] ?: return false

            val suggestedMovies = fetchMoviesByGenre(listOf(genreId)).shuffled().take(10)
            if (suggestedMovies.isEmpty()) return false

            group.genreMovieSuggestions = suggestedMovies.toMutableList()
            group.movieVotes = mutableMapOf()
            group.usersVoted = mutableMapOf()
            group.winnerMovie = Movie(
                id = "", title = "", release_date = "", overview = "", tagline = "",
                genres = listOf(), poster_path = "", vote_average = "0.0", trailer = null
            )
            group.votesSoFar = 0
            group.gameActive = true

            return updatePartyGroup(
                group, mapOf(
                    "genreMovieSuggestions" to group.genreMovieSuggestions,
                    "movieVotes" to group.movieVotes,
                    "usersVoted" to group.usersVoted,
                    "winnerMovie" to group.winnerMovie,
                    "votesSoFar" to group.votesSoFar,
                    "gameActive" to group.gameActive
                )
            )
        }
        suspend fun playExistingGame(groupId: Int, userId: String): Boolean {
            val group = getPartyGroup(groupId) ?: return false
            if (group.genreMovieSuggestions.isEmpty()) return false

            val userVotes = group.usersVoted.getOrPut(userId) { mutableListOf() }
           // group.usersVoted[userId] = userVotes
            group.gameActive = true

            return updatePartyGroup(group, mapOf(
                "gameActive" to true,
            ))
        }
    }

