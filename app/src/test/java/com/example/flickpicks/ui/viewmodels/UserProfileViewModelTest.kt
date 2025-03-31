package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.model.MovieReview
import com.example.flickpicks.data.model.UserProfile
import com.example.flickpicks.data.repository.UserProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class UserProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Mock
    lateinit var repository: UserProfileRepository

    @Mock
    lateinit var db: FirebaseFirestore

    private lateinit var viewModel: UserProfileViewModel

    private val testUser = UserProfile(
        id = "userProfile1",
        userName = "userProfile1",
        email = "userprofile1@example.com",
        followers = mutableListOf("friendA"),
        incomingRequests = mutableListOf("friendB"),
        outgoingRequests = mutableListOf(),
        moviesSaved = mutableListOf("Movie A"),
        moviesLiked = mutableListOf("Movie B"),
        moviesDisliked = mutableListOf("Movie C"),
        moviesReviewed = mutableListOf(
            MovieReview(
                id = 1,
                movieId = "1",
                movieTitle = "Movie D",
                release_date = "2025-03-31",
                tagline = "",
                overview = "Great movie",
                genres = listOf("Action"),
                reviewerName = "User Profile 1",
                reviewText = "Nice Movie",
                rating = 4,
                streamingPlatform = "Netflix"
            )
        )
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = UserProfileViewModel(repository, db)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addUserProfile() = runTest {
        viewModel.addUserProfile(testUser)
        advanceUntilIdle()
        verify(repository).addUserProfile(testUser)
    }

    @Test
    fun updateUserProfile() = runTest {
        val updates = mapOf("email" to "new@email.com")
        viewModel.updateUserProfile("userProfile1", updates)
        advanceUntilIdle()
        verify(repository).updateUserProfile("userProfile1", updates)
    }

    @Test
    fun fetchUserProfile() = runTest {
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()
        assertEquals(testUser, viewModel.userProfile.value)
    }

    @Test
    fun acceptFriendRequest() = runTest {
        val friend = testUser.copy(id = "friendA", followers = mutableListOf(), outgoingRequests = mutableListOf("user1"))
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        `when`(repository.getUserProfile("friendA")).thenReturn(friend)

        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()

        viewModel.acceptFriendRequest("friendA")
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.incomingRequests.contains("friendA"))
        assertTrue(viewModel.userProfile.value!!.followers.contains("friendA"))
    }

    @Test
    fun declineFriendRequest() = runTest {
        val friend = testUser.copy(id = "friendA", outgoingRequests = mutableListOf("userProfile1"))
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        `when`(repository.getUserProfile("friendA")).thenReturn(friend)

        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()

        viewModel.declineFriendRequest("friendA")
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.incomingRequests.contains("friendA"))
    }

    @Test
    fun removeFriend() = runTest {
        val friend = testUser.copy(id = "friendA", followers = mutableListOf("userProfile1"))
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        `when`(repository.getUserProfile("friendA")).thenReturn(friend)

        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()

        viewModel.removeFriend("friendA")
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.followers.contains("friendA"))
    }

    @Test
    fun removeSavedMovie() = runTest {
        `when`(repository.getUserProfile("userA")).thenReturn(testUser)
        viewModel.fetchUserProfile("userA")
        advanceUntilIdle()

        viewModel.removeSavedMovie("Movie A")
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.moviesSaved.contains("Movie A"))
    }

    @Test
    fun removeLikedMovie() = runTest {
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()

        viewModel.removeLikedMovie("Movie B")
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.moviesLiked.contains("Movie B"))
    }

    @Test
    fun removeDislikedMovie() = runTest {
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()

        viewModel.removeDislikedMovie("Movie C")
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.moviesDisliked.contains("Movie C"))
    }

    @Test
    fun removeReview() = runTest {
        val review = testUser.moviesReviewed.first()
        `when`(repository.getUserProfile("userProfile1")).thenReturn(testUser)
        viewModel.fetchUserProfile("userProfile1")
        advanceUntilIdle()

        viewModel.removeReview(review)
        advanceUntilIdle()

        assertFalse(viewModel.userProfile.value!!.moviesReviewed.contains(review))
    }
}


