package com.example.flickpicks

import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.PartyGroupInMemoryDatabase
import com.example.flickpicks.data.repository.PartyGroupRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyGroupRepositoryTest {
    private lateinit var repository: PartyGroupRepository
    private lateinit var mockDb: PartyGroupInMemoryDatabase

    @BeforeEach
    fun setUp() {
        mockDb = PartyGroupInMemoryDatabase()
        repository = PartyGroupRepository(mockDb)
    }

    @Test
    fun addPartyGroup() = runBlocking {
        val userId = "user1"
        val group = PartyGroup(
            id = 0,
            groupName = "Movie Night",
            members = mutableListOf(userId),
            timesAvailable = mutableMapOf(),
            pastWatchedMovies = mutableListOf(),
            chatMessages = mutableListOf(),
            genreMovieSuggestions = mutableListOf(),
            movieVotes = mutableMapOf(),
            usersVoted = mutableMapOf(),
            winnerMovie = Movie("", "", "", "", "", listOf(), "", "0.0", null),
            votesSoFar = 0,
            gameActive = false
        )

        val result = repository.addPartyGroup(group, userId)

        assertTrue(result)
        val storedGroup = mockDb.get(1)
        assertNotNull(storedGroup)
        assertEquals("Movie Night", storedGroup?.groupName)
    }
    @Test
    fun getPartyGroup() = runBlocking {
        val userId = "user1"
        val group = PartyGroup(
            id = 0,
            groupName = "Movie Night",
            members = mutableListOf(userId),
            timesAvailable = mutableMapOf(),
            pastWatchedMovies = mutableListOf(),
            chatMessages = mutableListOf(),
            genreMovieSuggestions = mutableListOf(),
            movieVotes = mutableMapOf(),
            usersVoted = mutableMapOf(),
            winnerMovie = Movie("", "", "", "", "", listOf(), "", "0.0", null),
            votesSoFar = 0,
            gameActive = false
        )

        repository.addPartyGroup(group, userId)
        val result = repository.getPartyGroup(1)

        assertNotNull(result)
        assertEquals("Movie Night", result?.groupName)
    }

    @Test
    fun updatePartyGroup() = runBlocking {
        val group = PartyGroup(groupName = "Old Name", members = mutableListOf("user1"))
        repository.addPartyGroup(group, "user1")
        val updated = repository.updatePartyGroup(group.copy(id = 1), mapOf("name" to "New Name"))
        assertTrue(updated)
    }

    @Test
    fun deletePartyGroup() = runBlocking {
        val group = PartyGroup(groupName = "Delete Me", members = mutableListOf("user1"))
        repository.addPartyGroup(group, "user1")
        val deleted = repository.deletePartyGroup(1)
        assertTrue(deleted)
        val fetched = repository.getPartyGroup(1)
        assertNull(fetched)
    }

    @Test
    fun sendAndGetChatMessages() = runBlocking {
    }

    @Test
    fun getUserPartyGroups() = runBlocking {
        val group1 = PartyGroup(groupName = "Group 1", members = mutableListOf("user1"))
        val group2 = PartyGroup(groupName = "Group 2", members = mutableListOf("user2"))
        repository.addPartyGroup(group1, "user1")
        repository.addPartyGroup(group2, "user2")

        val user1Groups = repository.getUserPartyGroups("user1")
        assertEquals(1, user1Groups.size)
        assertEquals("Group 1", user1Groups[0].groupName)
    }

    @Test
    fun voteOnMovie() = runBlocking {
        val group = PartyGroup(groupName = "Vote Group", members = mutableListOf("user1"), gameActive = true)
        val movie = Movie("1", "Test Movie", "2024", "Overview", "", listOf("Drama"), "", "8.5", null)
        group.genreMovieSuggestions = mutableListOf(movie)
        repository.addPartyGroup(group, "user1")

        val voted = repository.voteOnMovie(1, "user1", movie.id, true)
        assertTrue(voted)

        val updatedGroup = repository.getPartyGroup(1)
        assertFalse(updatedGroup!!.gameActive)
        assertEquals("Test Movie", updatedGroup.winnerMovie.title)
    }

    @Test
    fun startNewGame() = runBlocking {
        val group = PartyGroup(groupName = "New Game Group", members = mutableListOf("user1"))
        repository.addPartyGroup(group, "user1")

        val result = repository.startNewGame(
            id = 1,
            getUserProfile = { UserProfile(it, userName = "User", genrePreferences = mutableListOf("Action")) },
            fetchMoviesByGenre = { listOf(Movie("1", "Movie A", "2024", "", "", listOf("Action"), "", "8.0", null)) }
        )
        assertTrue(result)
        val updatedGroup = repository.getPartyGroup(1)
        assertEquals(1, updatedGroup!!.genreMovieSuggestions.size)
    }

    @Test
    fun playExistingGame() = runBlocking {
        val group = PartyGroup(groupName = "Existing Game", members = mutableListOf("user1"))
        group.genreMovieSuggestions = mutableListOf(Movie("1", "Movie A", "", "", "", listOf("Action"), "", "", null))
        repository.addPartyGroup(group, "user1")

        val result = repository.playExistingGame(1, "user1")
        assertTrue(result)
        val updatedGroup = repository.getPartyGroup(1)
        assertTrue(updatedGroup!!.gameActive)
    }
}
