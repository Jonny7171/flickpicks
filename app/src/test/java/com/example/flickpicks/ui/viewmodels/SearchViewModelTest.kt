package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: MoviesRepository

    @Mock
    private lateinit var userRepository: UserProfileRepository

    private lateinit var viewModel: SearchViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(repository, userRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchMovies should update searchResults state`() = runTest {
        val query = "Avengers"
        val movies = listOf(
            Movie("1", "Avengers: Endgame", "2019-04-26", "After the snap...", "", listOf("Action"), "", "8.5", "url"),
            Movie("2", "Avengers: Infinity War", "2018-04-27", "Thanos begins...", "", listOf("Action"), "", "8.4", "url")
        )

        `when`(repository.searchMovie(query)).thenReturn(movies)

        viewModel.searchMovies(query)
        advanceUntilIdle()

        assertEquals(2, viewModel.searchResults.value.size)
        assertEquals("Avengers: Endgame", viewModel.searchResults.value[0].title)
    }

    @Test
    fun `searchMovies with empty result should update searchResults to empty`() = runTest {
        val query = "UnknownMovie"
        `when`(repository.searchMovie(query)).thenReturn(emptyList())

        viewModel.searchMovies(query)
        advanceUntilIdle()

        assertEquals(0, viewModel.searchResults.value.size)
    }

    @Test
    fun `getTrailer should return trailer URL`() = runTest {
        val movieId = "1"
        val trailerUrl = "https://youtube.com/watch?v=trailer"

        `when`(repository.getMovieTrailer(movieId)).thenReturn(trailerUrl)

        val result = viewModel.getTrailer(movieId)
        assertEquals(trailerUrl, result)
    }

    @Test
    fun `getTrailer should return null if no trailer available`() = runTest {
        val movieId = "unknown"

        `when`(repository.getMovieTrailer(movieId)).thenReturn(null)

        val result = viewModel.getTrailer(movieId)
        assertEquals(null, result)
    }
}
