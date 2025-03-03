package com.example.flickpicks

import com.example.flickpicks.data.model.Genre
import com.example.flickpicks.data.repository.GenreRepository
import com.example.flickpicks.data.repository.GenreInMemoryDatabase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenreRepositoryTest {
    private lateinit var repository: GenreRepository
    private lateinit var mockDb: GenreInMemoryDatabase

    @BeforeEach
    fun setUp() {
        // Set up the mock in-memory database
        mockDb = GenreInMemoryDatabase()
        repository = GenreRepository(mockDb)
    }

    @Test
    fun addGenre() = runBlocking {
        val genre = Genre(name = "Action", count = 10)
        val result = repository.addGenre(genre)
        Assertions.assertTrue(result)
        Assertions.assertNotNull(mockDb.get("Action"))
    }

    @Test
    fun getGenre() = runBlocking {
        val genre = Genre(name = "Action", count = 10)
        mockDb.add(genre)

        val result = repository.getGenre("Action")

        Assertions.assertNotNull(result)
        Assertions.assertEquals("Action", result?.name)
        Assertions.assertEquals(10, result?.count)
    }

    @Test
    fun deleteGenre() = runBlocking {
        val genre = Genre(name = "Action", count = 10)
        mockDb.add(genre)

        val result = repository.deleteGenre("Action")

        Assertions.assertTrue(result)
        Assertions.assertNull(mockDb.get("Action"))
    }

    @Test
    fun updateGenre() = runBlocking {
        val genre = Genre(name = "Action", count = 10)
        mockDb.add(genre)

        val updates = mapOf("name" to "Adventure", "count" to 15)
        val result = repository.updateGenre(genre, updates)
        Assertions.assertTrue(result)
    }
}