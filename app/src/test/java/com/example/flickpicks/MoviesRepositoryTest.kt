package com.example.flickpicks

import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.source.MoviesSource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MoviesRepositoryTest {

    @Mock
    private lateinit var moviesSource: MoviesSource

    private lateinit var moviesRepository: MoviesRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        moviesRepository = MoviesRepository(moviesSource)
    }

    @Test
    fun getTrendingMovies() = runBlocking {
        // Mock data
        val movies = listOf(
            Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5"),
            Movie("2", "Movie B", "2024-01-02", "Overview B", "", listOf("Drama"), "", "8.0")
        )

        // Mock API response
        `when`(moviesSource.getTrendingMovies()).thenReturn(movies)

        // Call function
        val result = moviesRepository.getTrendingMovies()

        // Verify results
        Assertions.assertEquals(2, result.size)
        Assertions.assertEquals("Movie A", result[0].title)
        Assertions.assertEquals("Movie B", result[1].title)
    }

    @Test
    fun getMovieDetails() = runBlocking {
        // Mock data
        val movie = Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5")

        // Mock API response
        `when`(moviesSource.getMovieDetails("1")).thenReturn(movie)

        // Call function
        val result = moviesRepository.getMovieDetails("1")

        // Verify results
        Assertions.assertNotNull(result)
        Assertions.assertEquals("1", result.id)
        Assertions.assertEquals("Movie A", result.title)
    }

    @Test
    fun getMovieWatchProviders() = runBlocking {
        // Mock data
        val providers = listOf("Netflix", "Hulu", "Disney+")

        // Mock API response
        `when`(moviesSource.getMovieWatchProviders("1")).thenReturn(providers)

        // Call function
        val result = moviesRepository.getMovieWatchProviders("1")

        // Verify results
        Assertions.assertEquals(3, result.size)
        Assertions.assertTrue(result.contains("Netflix"))
        Assertions.assertTrue(result.contains("Hulu"))
        Assertions.assertTrue(result.contains("Disney+"))
    }

    @Test
    fun getMoviesByGenres() = runBlocking {
        // Mock data
        val movies = listOf(
            Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5"),
            Movie("2", "Movie B", "2024-01-02", "Overview B", "", listOf("Drama"), "", "8.0")
        )

        // Mock API response
        `when`(moviesSource.getMoviesByGenres(listOf("Action"))).thenReturn(movies.filter { "Action" in it.genres })

        // Call function
        val result = moviesRepository.getMoviesByGenres(listOf("Action"))

        // Verify results
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals("Movie A", result[0].title)
    }
}