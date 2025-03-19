package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.model.GENRE_MAP
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class MyFeedViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var moviesRepository: MoviesRepository

    @Mock
    private lateinit var userProfileRepository: UserProfileRepository

    private lateinit var viewModel: MyFeedViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MyFeedViewModel(moviesRepository, userProfileRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchTrendingMovies should update trendingMovies state`(): Unit = runTest {
        val movies = listOf(
            Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5", "url"),
            Movie("2", "Movie B", "2024-01-02", "Overview B", "", listOf("Drama"), "", "8.0", "url")
        )

        `when`(moviesRepository.getTrendingMovies()).thenReturn(movies)

        viewModel = MyFeedViewModel(moviesRepository, userProfileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        Assertions.assertEquals(movies, viewModel.trendingMovies.value)
    }

    @Test
    fun `fetchRecommendedMovies should update recommendedMovies state`() = runTest {
        val userId = "user1"
        val userProfile = UserProfile(userId, "User", "", "", "", "", "", genrePreferences = mutableListOf("Action"))

        val movies = listOf(
            Movie("1", "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5", "url")
        )

        val expectedGenreIds = listOf(GENRE_MAP["action"]!!)

        `when`(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
        `when`(moviesRepository.getMoviesByGenres(expectedGenreIds)).thenReturn(movies)

        viewModel.fetchRecommendedMovies(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        println("Recommended Movies: ${viewModel.recommendedMovies.value}")

        Assertions.assertEquals(1, viewModel.recommendedMovies.value.size)
        Assertions.assertEquals("Movie A", viewModel.recommendedMovies.value[0].title)
    }

    @Test
    fun `saveLikedMovie should update likedMovies state`() = runTest {
        val userId = "user1"
        val movieName = "Movie A"
        val userProfile = UserProfile(userId, "User", "", "", "", "", "")

        `when`(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)

        viewModel.saveLikedMovie(userId, movieName, remove = false)
        testDispatcher.scheduler.advanceUntilIdle()

        Assertions.assertTrue(viewModel.likedMovies.value[movieName] == true)
    }

    @Test
    fun `saveDislikedMovie should update dislikedMovies state`() = runTest {
        val userId = "user1"
        val movieName = "Movie B"
        val userProfile = UserProfile(userId, "User", "", "", "", "", "")

        `when`(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)

        viewModel.saveDislikedMovie(userId, movieName, remove = false)
        testDispatcher.scheduler.advanceUntilIdle()

        Assertions.assertTrue(viewModel.dislikedMovies.value[movieName] == true)
    }

    @Test
    fun `saveMovie should update savedMovies state`() = runTest {
        val userId = "user1"
        val movieName = "Movie C"
        val userProfile = UserProfile(userId, "User", "", "", "", "", "")

        `when`(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)

        viewModel.saveMovie(userId, movieName)
        testDispatcher.scheduler.advanceUntilIdle()

        Assertions.assertTrue(viewModel.savedMovies.value[movieName] == true)
    }

    @Test
    fun `getMovieDetails should update selectedMovie state`() = runTest {
        val movieId = "1"
        val movie = Movie(movieId, "Movie A", "2024-01-01", "Overview A", "", listOf("Action"), "", "7.5", "url")

        `when`(moviesRepository.getMovieDetails(movieId)).thenReturn(movie)

        viewModel.getMovieDetails(movieId)
        testDispatcher.scheduler.advanceUntilIdle()

        Assertions.assertEquals(movie, viewModel.selectedMovie.value)
    }

    @Test
    fun `fetchWatchProviders should update watchProviders state`() = runTest {
        val movieId = "1"
        val providers = listOf("Netflix", "Hulu")

        `when`(moviesRepository.getMovieWatchProviders(movieId)).thenReturn(providers)

        viewModel.fetchWatchProviders(movieId)
        testDispatcher.scheduler.advanceUntilIdle()

        Assertions.assertEquals(providers, viewModel.watchProviders.value)
    }

//    @Test
//    fun `fetchReviewedByFriends should update moviesReviewedByFriends state`() = runTest {
//        val userId = "user1"
//        val friendId = "friend1"
//
//        val review = MovieReview(
//            movieId = "1",
//            movieTitle = "Movie A",
//            release_date = "2024-01-01",
//            tagline = "",
//            overview = "Overview A",
//            genres = listOf("Action"),
//            reviewerName = "Friend",
//            reviewText = "Amazing movie!",
//            rating = 5,
//            streamingPlatform = "Netflix"
//        )
//
//        val userProfile = UserProfile(userId, "User", "", "", "", "", "")
//        val friendProfile = UserProfile(friendId, "Friend", "", "", "", "", "")
//
//        `when`(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
//        `when`(userProfileRepository.getUserProfile(friendId)).thenReturn(friendProfile)
//
//        viewModel.fetchReviewedByFriends(userId)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        Assertions.assertEquals(1, viewModel.moviesReviewedByFriends.value.size)
//        Assertions.assertEquals("Movie A", viewModel.moviesReviewedByFriends.value[0].movieTitle)
//    }
}