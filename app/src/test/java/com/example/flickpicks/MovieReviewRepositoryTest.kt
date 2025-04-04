package com.example.flickpicks

import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.repository.MovieReviewInMemoryDatabase
import com.example.flickpicks.data.repository.MovieReviewRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MovieReviewRepositoryTest {
    private lateinit var repository: MovieReviewRepository
    private lateinit var mockDb: MovieReviewInMemoryDatabase

    @BeforeEach
    fun setUp() {
        mockDb = MovieReviewInMemoryDatabase()
        repository = MovieReviewRepository(mockDb)
    }

    @Test
    fun addMovieReview() = runBlocking {
        val review = MovieReview(
            id = 104,
            movieTitle = "B99",
            release_date = "2010-07-16",
            tagline = "Bad detective",
            overview = "A bad detective",
            genres = listOf("Mystery"),
            reviewerName = "neha",
            reviewText = "Amazing show!",
            rating = 5,
            streamingPlatform = "Netflix"
        )
        val result = repository.addMovieReview(review)
        Assertions.assertTrue(result)
        Assertions.assertNotNull(mockDb.get(104))
    }

    @Test
    fun getMovieReview() = runBlocking {
        val review = MovieReview(
            id = 104,
            movieTitle = "B99",
            release_date = "2010-07-16",
            tagline = "Bad detective",
            overview = "A bad detective",
            genres = listOf("Mystery"),
            reviewerName = "neha",
            reviewText = "Amazing show!",
            rating = 5,
            streamingPlatform = "Netflix"
        )
        mockDb.add(review)

        val result = repository.getMovieReview(104)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(104, result?.id)
        Assertions.assertEquals("B99", result?.movieTitle)
    }

    @Test
    fun updateMovieReview() = runBlocking {
        val review = MovieReview(
            id = 104,
            movieTitle = "B99",
            release_date = "2010-07-16",
            tagline = "Bad detective",
            overview = "A bad detective",
            genres = listOf("Mystery"),
            reviewerName = "neha",
            reviewText = "Amazing show!",
            rating = 5,
            streamingPlatform = "Netflix"
        )
        mockDb.add(review)
        val updates = mapOf("tagline" to "Reality check for teenage adults", "reviwerName" to "Test Review")
        val result = repository.updateMovieReview(review, updates)
        Assertions.assertTrue(result)
    }

    @Test
    fun deleteMovieReview() = runBlocking {
        val review = MovieReview(
            id = 104,
            movieTitle = "B99",
            release_date = "2010-07-16",
            tagline = "Bad detective",
            overview = "A bad detective",
            genres = listOf("Mystery"),
            reviewerName = "neha",
            reviewText = "Amazing show!",
            rating = 5,
            streamingPlatform = "Netflix"
        )
        mockDb.add(review)
        val result = repository.deleteMovieReview(104)
        Assertions.assertTrue(result)
        Assertions.assertNull(mockDb.get(104))
    }
}
