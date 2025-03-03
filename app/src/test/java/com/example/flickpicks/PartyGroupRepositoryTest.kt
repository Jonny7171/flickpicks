package com.example.flickpicks

import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.repository.PartyGroupInMemoryDatabase
import com.example.flickpicks.data.repository.PartyGroupRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PartyGroupRepositoryTest {

    private lateinit var repository: PartyGroupRepository
    private lateinit var mockDb: PartyGroupInMemoryDatabase

    @BeforeEach
    fun setUp() {
        // Set up the mock in-memory database
        mockDb = PartyGroupInMemoryDatabase()
        repository = PartyGroupRepository(mockDb)
    }

    @Test
    fun addPartyGroup() = runBlocking {
        val group = PartyGroup(id = 5, name = "CS Party")
        val result = repository.addPartyGroup(group)
        Assertions.assertTrue(result)
        Assertions.assertNotNull(mockDb.get(5))
    }

    @Test
    fun getPartyGroup() = runBlocking{
        val group = PartyGroup(id = 5, name = "CS Party")
        mockDb.add(group)
        val result = repository.getPartyGroup(5)
        Assertions.assertNotNull(result)
        Assertions.assertEquals(5, result?.id)
        Assertions.assertEquals("CS Party", result?.name)
    }

    @Test
    fun deletePartyGroup() = runBlocking {
        val group = PartyGroup(id = 5, name = "CS Party")
        mockDb.add(group)
        val result = repository.deletePartyGroup(5)
        Assertions.assertTrue(result)
        Assertions.assertNull(mockDb.get(5))
    }

    @Test
    fun updatePartyGroup() = runBlocking {
        val group = PartyGroup(id = 5, name = "CS Party")
        mockDb.add(group)
        val updates = mapOf("name" to "Post Exam Party")
        val result = repository.updatePartyGroup(group, updates)
        Assertions.assertTrue(result)
    }
}