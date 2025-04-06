package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.data.model.Movie
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.PartyGroupRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class PartyGroupViewModelTest {

    @get:Rule

    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock lateinit var userRepo: UserProfileRepository
    @Mock lateinit var moviesRepo: MoviesRepository
    @Mock lateinit var partyGroupRepo: PartyGroupRepository

    private lateinit var partyGroupVM: PartyGroupViewModel

    private lateinit var firestoreStatic: MockedStatic<FirebaseFirestore>
    private lateinit var authStatic: MockedStatic<FirebaseAuth>

    private val mockFirestore = mock(FirebaseFirestore::class.java)
    private val mockAuth = mock(FirebaseAuth::class.java)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        firestoreStatic = mockStatic(FirebaseFirestore::class.java)
        authStatic = mockStatic(FirebaseAuth::class.java)

        firestoreStatic.`when`<Any> { FirebaseFirestore.getInstance() }.thenReturn(mockFirestore)
        authStatic.`when`<Any> { FirebaseAuth.getInstance() }.thenReturn(mockAuth)
        partyGroupVM = PartyGroupViewModel(userRepo, moviesRepo, partyGroupRepo)

    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        firestoreStatic.close()
        authStatic.close()
    }

    @Test
    fun `addPartyGroup should add new group and populate the party list`() = runTest {
        val testGroup = PartyGroup(
            id = partyGroupVM.userPartyGroups.size + 1,
            groupName = "testing group",
            members = mutableListOf("user1", "user2"),
            timesAvailable = mutableMapOf(),
            winnerMovie = Movie(
                id = "0",
                title = "",
                release_date = "",
                overview = "",
                tagline = "",
                genres = listOf(),
                poster_path = "",
                vote_average = "0.0",
                trailer = ""
            ),
            pastWatchedMovies = mutableListOf(),
            chatMessages = mutableListOf()
        )
        val testUser = "testUser100000"

        val testCopyGroup = testGroup.copy(id = 40000)

        runBlocking {
            whenever(partyGroupRepo.getTotalPartyGroupsCount()).thenReturn(2)
            whenever(partyGroupRepo.addPartyGroup(eq(testCopyGroup), eq(testUser))).then { }
            whenever(partyGroupRepo.getUserPartyGroups(eq(testUser))).thenReturn(listOf(testCopyGroup))
        }

        partyGroupVM.addPartyGroup(testGroup, testUser)

        advanceUntilIdle()

        verifyBlocking(partyGroupRepo) {
            getTotalPartyGroupsCount()
            addPartyGroup(testCopyGroup, testUser)
            getUserPartyGroups(testUser)
        }

        assertEquals(1, partyGroupVM.userPartyGroups.size)
        assertEquals("testing group", partyGroupVM.userPartyGroups[0].groupName)
        assertEquals(listOf("user1", "user2"), partyGroupVM.userPartyGroups[0].members)
    }

    @Test
    fun `deletePartyGroup should delete it from party list`() = runTest {
        val testGroup = PartyGroup(
            id = 100000,
            groupName = "testing delete group",
            members = mutableListOf("no users"),
            timesAvailable = mutableMapOf(),
            winnerMovie = Movie(
                id = "0",
                title = "",
                release_date = "",
                overview = "",
                tagline = "",
                genres = listOf(),
                poster_path = "",
                vote_average = "0.0",
                trailer = ""
            ),
            pastWatchedMovies = mutableListOf(),
            chatMessages = mutableListOf()
        )

        partyGroupVM.apply {
            val field = this::class.java.getDeclaredField("_userPartyGroups")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val list = field.get(this) as MutableList<PartyGroup>
            list.add(testGroup)
        }

        runBlocking {
            whenever(partyGroupRepo.deletePartyGroup(testGroup.id)).then { }
        }

        partyGroupVM.deletePartyGroup(testGroup)
        advanceUntilIdle()
        verify(partyGroupRepo).deletePartyGroup(100000)

    }

    @Test
    fun `sendMessage adds message onto chatMessages list and updates list`() = runTest {
        val groupID = 100000
        val testMsg = ChatMessage("TestUser", "hi123", false, System.currentTimeMillis())

        runBlocking {
            whenever(partyGroupRepo.sendChatMessage(groupID, testMsg)).then { }
        }

        partyGroupVM.sendMessage(groupID, testMsg)

        advanceUntilIdle()

        verifyBlocking(partyGroupRepo) {
            sendChatMessage(groupID, testMsg)
        }

        assertEquals(1, partyGroupVM.messages.value.size)
        assertEquals("TestUser", partyGroupVM.messages.value[0].sender)
        assertEquals("hi123", partyGroupVM.messages.value[0].message)
    }

    @Test
    fun `voteOnMovie votes on movies`() = runTest {
        val groupID = 100000
        val testUser = "uid123"
        val testMovie = "testMovie 345"
        val testVote = true

        runBlocking {
            whenever(partyGroupRepo.voteOnMovie(groupID, testUser, testMovie, testVote)).then { }
        }

        partyGroupVM.voteOnMovie(groupID, testUser, testMovie, testVote)

        advanceUntilIdle()

        verifyBlocking(partyGroupRepo) {
            voteOnMovie(groupID, testUser, testMovie, testVote)
        }
    }
}
