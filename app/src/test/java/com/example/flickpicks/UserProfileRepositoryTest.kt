package com.example.flickpicks

import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.UserProfileInMemoryDatabase
import com.example.flickpicks.data.repository.UserProfileRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserProfileRepositoryTest {
    private lateinit var repository: UserProfileRepository
    private lateinit var mockDb: UserProfileInMemoryDatabase

    @BeforeEach
    fun setUp() {
        // Set up the mock in-memory database
        mockDb = UserProfileInMemoryDatabase()
        repository = UserProfileRepository(mockDb)
    }

    @Test
    fun addUserProfile() = runBlocking {
        val user = UserProfile(
            id = 5,
            name = "Shaili Doe",
            email = "Jim.doe@example.com",
            userName = "jimdoe"
        )
        val result = repository.addUserProfile(user)
        Assertions.assertTrue(result)
        Assertions.assertNotNull(mockDb.get(5))
    }

    @Test
    fun getUserProfile() = runBlocking{
        val user = UserProfile(
            id = 5,
            name = "Shaili Doe",
            email = "Jim.doe@example.com",
            userName = "jimdoe"
        )
        mockDb.add(user)

        val result = repository.getUserProfile(5)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(5, result?.id)
        Assertions.assertEquals("Shaili Doe", result?.name)
    }

    @Test
    fun deleteUserProfile() = runBlocking {
        val user = UserProfile(
            id = 5,
            name = "Shaili Doe",
            email = "Jim.doe@example.com",
            userName = "jimdoe"
        )

        mockDb.add(user)
        val result = repository.deleteUserProfile(5)
        Assertions.assertTrue(result)
        Assertions.assertNull(mockDb.get(5))

    }

    @Test
    fun updateUserProfile() = runBlocking {
        val user = UserProfile(
            id = 5,
            name = "Shaili Doe",
            email = "Jim.doe@example.com",
            userName = "jimdoe"
        )
        mockDb.add(user)
        val updates = mapOf("email" to "shailidoe@example.com", "username" to "SK")
        val result = repository.updateUserProfile(user, updates)
        Assertions.assertTrue(result)
    }
}