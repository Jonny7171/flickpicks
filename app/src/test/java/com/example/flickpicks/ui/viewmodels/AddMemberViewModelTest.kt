package com.example.flickpicks.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.flickpicks.data.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import com.google.android.gms.tasks.OnSuccessListener

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class AddMemberViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Mock
    lateinit var firestore: FirebaseFirestore

    @Mock
    lateinit var collectionReference: CollectionReference

    @Mock
    lateinit var query: Query

    @Mock
    lateinit var task: Task<QuerySnapshot>

    @Mock
    lateinit var snapshot: QuerySnapshot

    private lateinit var viewModel: AddMemberViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = AddMemberViewModel(firestore)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchUsers with blank query returns empty userList`() = runTest {
        viewModel.searchUsers("   ") // blank input
        advanceUntilIdle()
        assertEquals(emptyList<UserProfile>(), viewModel.userList.value)
    }

    @Test
    fun `searchUsers builds correct Firestore query chain`() = runTest {
        // Arrange
        `when`(firestore.collection("users")).thenReturn(collectionReference)
        `when`(collectionReference.orderBy("userName")).thenReturn(query)
        `when`(query.startAt("A")).thenReturn(query)
        `when`(query.endAt("A\uF8FF")).thenReturn(query)
        `when`(query.get()).thenReturn(task)
        `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<QuerySnapshot>
            listener.onSuccess(snapshot)
            task
        }

        `when`(snapshot.documents).thenReturn(emptyList())
        viewModel.searchUsers("A")
        advanceUntilIdle()
        verify(firestore).collection("users")
        verify(collectionReference).orderBy("userName")
        verify(query).startAt("A")
        verify(query).endAt("A\uF8FF")
        verify(query).get()
    }
}
