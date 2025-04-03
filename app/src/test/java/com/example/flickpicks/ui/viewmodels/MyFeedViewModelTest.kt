package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.model.GENRE_MAP
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.MovieReviewRepository
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever
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

    @Mock
    private lateinit var reviewRepository: MovieReviewRepository

    private lateinit var viewModel: MyFeedViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MyFeedViewModel(moviesRepository, userProfileRepository, reviewRepository)
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

        viewModel = MyFeedViewModel(moviesRepository, userProfileRepository, reviewRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(movies, viewModel.trendingMovies.value)
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

        assertEquals(1, viewModel.recommendedMovies.value.size)
        assertEquals("Movie A", viewModel.recommendedMovies.value[0].title)
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

        assertEquals(movie, viewModel.selectedMovie.value)
    }

    @Test
    fun `fetchWatchProviders should update watchProviders state`() = runTest {
        val movieId = "1"
        val providers = listOf("Netflix", "Hulu")

        `when`(moviesRepository.getMovieWatchProviders(movieId)).thenReturn(providers)

        viewModel.fetchWatchProviders(movieId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(providers, viewModel.watchProviders.value)
    }

    @Test
    fun `fetchReviewedByFriends should update moviesReviewedByFriends state`() = runTest {
        val userId = "user1"
        val friendId = "friend1"

        val review = MovieReview(
            id = 1,
            movieId = "1",
            movieTitle = "Movie A",
            release_date = "2024-01-01",
            tagline = "",
            overview = "Overview A",
            genres = listOf("Action"),
            reviewerName = "Friend",
            reviewText = "Amazing movie!",
            rating = 5,
            streamingPlatform = "Netflix"
        )

        val userProfile = UserProfile(userId, "User", "", "", "", "", followers = mutableListOf(friendId))
        val friendProfile = UserProfile(friendId, "Friend", "", "", "", "", moviesReviewed = mutableListOf(review), followers = mutableListOf(userId) )

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
        whenever(userProfileRepository.getUserProfile(friendId)).thenReturn(friendProfile)

        viewModel.fetchReviewedByFriends(userId)
        advanceUntilIdle()

        assertEquals(1, viewModel.moviesReviewedByFriends.value.size)
        assertEquals("Movie A", viewModel.moviesReviewedByFriends.value[0].movieTitle)
    }

    @Test
    fun `postReview should add new review if no existing review`() = runTest {
        val userId = "user1"
        val movieId = "1"
        val rating = "4"
        val reviewText = "Great movie!"
        val whereWatched = "Netflix"

        val movie = Movie(
            movieId, "Movie A", "2024-01-01", "", "Overview A", listOf("Action"),
            poster_path = "",
            vote_average = "",
            trailer = ""
        )
        val userProfile = UserProfile(userId, "User", "", "", "", "")

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
        whenever(moviesRepository.getMovieDetails(movieId)).thenReturn(movie)

        viewModel.postReview(userId, movieId, rating, reviewText, whereWatched)
        advanceUntilIdle()

        verify(reviewRepository).addMovieReview(argThat { review ->
            assertEquals(0, review.id)
            assertEquals("Movie A", review.movieTitle)
            assertEquals(4, review.rating)
            true
        })
    }

    @Test
    fun `postReview should update existing review instead of creating a new one`() = runTest {
        val userId = "user1"
        val movieId = "1"
        val rating = "5"
        val reviewText = "Even better on second watch!"
        val whereWatched = "Netflix"

        val movie = Movie(
            movieId, "Movie A", "2024-01-01", "", "Overview A", listOf("Action"),
            poster_path = "",
            vote_average = "",
            trailer = ""
        )
        val existingReview = MovieReview(
            id = 1, movieId = movieId, movieTitle = "Movie A", release_date = "2024-01-01",
            tagline = "", overview = "Overview A", genres = listOf("Action"), reviewerName = "User",
            reviewText = "Good movie!", rating = 4, streamingPlatform = "Netflix"
        )
        val userProfile = UserProfile(userId, "User", "", "", "", "", moviesReviewed = mutableListOf(existingReview))

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
        whenever(moviesRepository.getMovieDetails(movieId)).thenReturn(movie)

        viewModel.postReview(userId, movieId, rating, reviewText, whereWatched)
        advanceUntilIdle()

        verify(reviewRepository).deleteMovieReview(existingReview.id)
        verify(reviewRepository).addMovieReview(argThat { review ->
            assertEquals(1, review.id)
            assertEquals("Movie A", review.movieTitle)
            assertEquals(5, review.rating)
            true
        })
    }

    @Test
    fun `getCurrUserMovieReview should return review if user has reviewed movie`() = runTest {
        val userId = "user1"
        val movieId = "movie123"

        val review = MovieReview(
            id = 1,
            movieId = movieId,
            movieTitle = "Movie A",
            release_date = "2024-01-01",
            tagline = "",
            overview = "Great movie",
            genres = listOf("Action"),
            reviewerName = "User",
            reviewText = "Loved it!",
            rating = 5,
            streamingPlatform = "Netflix"
        )

        val userProfile = UserProfile(userId, "User", "", "", "", "", "", moviesReviewed = mutableListOf(review))

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)

        val result = viewModel.getCurrUserMovieReview(userId, movieId)

        assertEquals(review, result)
    }

    @Test
    fun `getCurrUserMovieReview should return null if user has not reviewed movie`() = runTest {
        val userId = "user1"
        val movieId = "movie123"

        val userProfile = UserProfile(userId, "User", "", "", "", "", "", moviesReviewed = mutableListOf())

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)

        val result = viewModel.getCurrUserMovieReview(userId, movieId)

        assertEquals(null, result)
    }

    @Test
    fun `getFriendsMovieReviews should return reviews from friends who reviewed the movie`() = runTest {
        val userId = "user1"
        val friendId = "friend1"
        val movieId = "movie123"

        val review = MovieReview(
            id = 1,
            movieId = movieId,
            movieTitle = "Movie A",
            release_date = "2024-01-01",
            tagline = "",
            overview = "Awesome movie",
            genres = listOf("Action"),
            reviewerName = "Friend",
            reviewText = "Really enjoyed it!",
            rating = 4,
            streamingPlatform = "Prime Video"
        )

        val friendProfile = UserProfile(friendId, "Friend", "", "", "", "", "", moviesReviewed = mutableListOf(review))
        val userProfile = UserProfile(userId, "User", "", "", "", "", "", followers = mutableListOf(friendId))

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
        whenever(userProfileRepository.getUserProfile(friendId)).thenReturn(friendProfile)

        val result = viewModel.getFriendsMovieReviews(userId, movieId)

        assertEquals(listOf(review), result)
    }

    @Test
    fun `getFriendsMovieReviews should return empty list if no friends reviewed the movie`() = runTest {
        val userId = "user1"
        val friendId = "friend1"
        val movieId = "movie123"

        val friendProfile = UserProfile(friendId, "Friend", "", "", "", "", "", moviesReviewed = mutableListOf())
        val userProfile = UserProfile(userId, "User", "", "", "", "", "", followers = mutableListOf(friendId))

        whenever(userProfileRepository.getUserProfile(userId)).thenReturn(userProfile)
        whenever(userProfileRepository.getUserProfile(friendId)).thenReturn(friendProfile)

        val result = viewModel.getFriendsMovieReviews(userId, movieId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getTrailer should return trailer URL if available`() = runTest {
        val movieId = "1"
        val trailerUrl = "https://youtube.com/watch?v=123"

        whenever(moviesRepository.getMovieTrailer(movieId)).thenReturn(trailerUrl)

        val result = viewModel.getTrailer(movieId)

        assertEquals(trailerUrl, result)
    }

    @Test
    fun `getTrailer should return null if no trailer is available`() = runTest {
        val movieId = "1"

        whenever(moviesRepository.getMovieTrailer(movieId)).thenReturn(null)

        val result = viewModel.getTrailer(movieId)

        assertEquals(null, result)
    }

    @Test
    fun `getMovieReviews should return list of reviews if available`() = runTest {
        val movieId = "1"
        val reviews = listOf("User A" to "Great!", "User B" to "Amazing!")

        whenever(moviesRepository.getMovieReviews(movieId)).thenReturn(reviews)

        val result = viewModel.getMovieReviews(movieId)

        assertEquals(reviews, result)
    }

    @Test
    fun `getMovieReviews should return null if no reviews are available`() = runTest {
        val movieId = "1"

        whenever(moviesRepository.getMovieReviews(movieId)).thenReturn(null)

        val result = viewModel.getMovieReviews(movieId)

        assertEquals(null, result)
    }
}
