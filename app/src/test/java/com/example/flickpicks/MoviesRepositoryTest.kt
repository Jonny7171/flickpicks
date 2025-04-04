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
        val movies = listOf(
            Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5", "url"),
            Movie("2", "Movie B", "2024-01-02", "Overview B", "", listOf("Drama"), "", "8.0", "url")
        )

        `when`(moviesSource.getTrendingMovies()).thenReturn(movies)

        val result = moviesRepository.getTrendingMovies()

        Assertions.assertEquals(2, result.size)
        Assertions.assertEquals("Movie A", result[0].title)
        Assertions.assertEquals("Movie B", result[1].title)
    }

    @Test
    fun getMovieDetails() = runBlocking {
        val movie = Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5", "url")

        `when`(moviesSource.getMovieDetails("1")).thenReturn(movie)

        val result = moviesRepository.getMovieDetails("1")

        Assertions.assertNotNull(result)
        Assertions.assertEquals("1", result.id)
        Assertions.assertEquals("Movie A", result.title)
    }

    @Test
    fun getMovieWatchProviders() = runBlocking {
        val providers = listOf("Netflix", "Hulu", "Disney+")

        `when`(moviesSource.getMovieWatchProviders("1")).thenReturn(providers)

        val result = moviesRepository.getMovieWatchProviders("1")

        Assertions.assertEquals(3, result.size)
        Assertions.assertTrue(result.contains("Netflix"))
        Assertions.assertTrue(result.contains("Hulu"))
        Assertions.assertTrue(result.contains("Disney+"))
    }

    @Test
    fun getMoviesByGenres() = runBlocking {
        val movies = listOf(
            Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5", "url"),
            Movie("2", "Movie B", "2024-01-02", "Overview B", "", listOf("Drama"), "", "8.0", "url")
        )

        `when`(moviesSource.getMoviesByGenres(listOf("Action"))).thenReturn(movies.filter { "Action" in it.genres })

        val result = moviesRepository.getMoviesByGenres(listOf("Action"))

        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals("Movie A", result[0].title)
    }

    @Test
    fun getMovieTrailer() = runBlocking {
        val trailerUrl = "https://youtube.com/trailer123"
        `when`(moviesSource.getMovieTrailer("1")).thenReturn(trailerUrl)

        val result = moviesRepository.getMovieTrailer("1")

        Assertions.assertNotNull(result)
        Assertions.assertEquals(trailerUrl, result)
    }

    @Test
    fun getMovieTrailer_NoTrailerAvailable() = runBlocking {
        `when`(moviesSource.getMovieTrailer("1")).thenReturn(null)

        val result = moviesRepository.getMovieTrailer("1")

        Assertions.assertNull(result)
    }

    @Test
    fun getMovieReviews() = runBlocking {
        val reviews = listOf(
            Pair("Alice", "Great movie!"),
            Pair("Bob", "Not bad."),
            Pair("Charlie", "Loved it!")
        )
        `when`(moviesSource.getMovieReviews("1")).thenReturn(reviews)

        val result = moviesRepository.getMovieReviews("1")

        Assertions.assertNotNull(result)
        Assertions.assertEquals(3, result?.size)
        Assertions.assertEquals("Alice", result?.get(0)?.first)
        Assertions.assertEquals("Great movie!", result?.get(0)?.second)
    }

    @Test
    fun getMovieReviews_NoReviews() = runBlocking {
        `when`(moviesSource.getMovieReviews("1")).thenReturn(emptyList())

        val result = moviesRepository.getMovieReviews("1")

        Assertions.assertNotNull(result)
        Assertions.assertTrue(result!!.isEmpty())
    }
    @Test
    fun searchMovies() = runBlocking {
        val searchQuery = "Inception"
        val movies = listOf(
            Movie("1", "Inception", "2010-07-16", "A mind-bending thriller", "", listOf("Sci-Fi", "Thriller"), "", "8.8", "url"),
            Movie("2", "Interstellar", "2014-11-07", "Exploring space and time", "", listOf("Sci-Fi", "Adventure"), "", "8.6", "url")
        )

        `when`(moviesSource.searchMovies(searchQuery)).thenReturn(movies)

        val result = moviesRepository.searchMovie(searchQuery)

        Assertions.assertEquals(2, result.size)
        Assertions.assertEquals("Inception", result[0].title)
        Assertions.assertEquals("Interstellar", result[1].title)
    }

    @Test
    fun searchMovies_NoResults() = runBlocking {
        val searchQuery = "NonExistentMovie"

        `when`(moviesSource.searchMovies(searchQuery)).thenReturn(emptyList())

        val result = moviesRepository.searchMovie(searchQuery)

        Assertions.assertNotNull(result)
        Assertions.assertTrue(result.isEmpty())
    }
}
