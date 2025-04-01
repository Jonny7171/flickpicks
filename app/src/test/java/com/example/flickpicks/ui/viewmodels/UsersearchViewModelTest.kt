package com.example.flickpicks.ui.viewmodels

import com.example.flickpicks.data.model.UserProfile
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.mockStatic
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class)
class UserSearchViewModelTest {

    @Mock lateinit var firestore: FirebaseFirestore
    @Mock lateinit var auth: FirebaseAuth
    @Mock lateinit var firebaseUser: FirebaseUser
    @Mock lateinit var collectionRef: CollectionReference
    @Mock lateinit var query: Query
    @Mock lateinit var task: Task<QuerySnapshot>
    @Mock lateinit var snapshot: QuerySnapshot

    @Mock lateinit var documentRef: DocumentReference
    @Mock lateinit var documentSnapshotTask: Task<DocumentSnapshot>
    @Mock lateinit var documentSnapshot: DocumentSnapshot
    @Mock lateinit var updateTask: Task<Void>

    private val mockCurrentUserId = "currentUser123"
    private val targetUser = UserProfile(id = "targetUser456", userName = "Target", email = "target@example.com")

    @Test
    fun `searchUsers with blank query clears userList`() {
        val viewModel = UserSearchViewModel()
        viewModel.userList.value = listOf(UserProfile("1", "Alex", "a@a.com"))
        viewModel.searchUsers("   ")
        assertEquals(emptyList(), viewModel.userList.value)
    }

    @Test
    fun `searchUsers builds correct Firestore query`() = runTest {
        mockStatic(FirebaseFirestore::class.java).use { firestoreStatic ->
            firestoreStatic.`when`<Any> { FirebaseFirestore.getInstance() }.thenReturn(firestore)

            `when`(firestore.collection("users")).thenReturn(collectionRef)
            `when`(collectionRef.orderBy("userName")).thenReturn(query)
            `when`(query.startAt("A")).thenReturn(query)
            `when`(query.endAt("A\uF8FF")).thenReturn(query)
            `when`(query.get()).thenReturn(task)

            `when`(task.addOnSuccessListener(any())).thenAnswer { invocation ->
                val listener = invocation.arguments[0] as OnSuccessListener<QuerySnapshot>
                listener.onSuccess(snapshot)
                task
            }

            `when`(snapshot.documents).thenReturn(emptyList())

            val viewModel = UserSearchViewModel()
            viewModel.searchUsers("A")

            verify(firestore).collection("users")
            verify(collectionRef).orderBy("userName")
            verify(query).startAt("A")
            verify(query).endAt("A\uF8FF")
            verify(query).get()
        }
    }

    @Test
    fun `sendFriendRequest sends request if not already sent`() = runTest {
        mockStatic(FirebaseFirestore::class.java).use { firestoreStatic ->
            firestoreStatic.`when`<Any> { FirebaseFirestore.getInstance() }.thenReturn(firestore)

            mockStatic(FirebaseAuth::class.java).use { authStatic ->
                authStatic.`when`<Any> { FirebaseAuth.getInstance() }.thenReturn(auth)

                `when`(auth.currentUser).thenReturn(firebaseUser)
                `when`(firebaseUser.uid).thenReturn(mockCurrentUserId)

                `when`(firestore.collection("users")).thenReturn(collectionRef)
                `when`(collectionRef.document(mockCurrentUserId)).thenReturn(documentRef)
                `when`(collectionRef.document(targetUser.id)).thenReturn(documentRef)
                `when`(documentRef.get()).thenReturn(documentSnapshotTask)

                `when`(documentSnapshotTask.addOnSuccessListener(any())).thenAnswer { invocation ->
                    val listener = invocation.arguments[0] as OnSuccessListener<DocumentSnapshot>
                    `when`(documentSnapshot.get("outgoingRequests")).thenReturn(emptyList<String>())
                    listener.onSuccess(documentSnapshot)
                    documentSnapshotTask
                }

                `when`(documentRef.update(anyString(), any())).thenReturn(updateTask)
                `when`(updateTask.addOnSuccessListener(any())).thenAnswer {
                    val listener = it.arguments[0] as OnSuccessListener<Void>
                    listener.onSuccess(null)
                    updateTask
                }

                val viewModel = UserSearchViewModel()

                var success = false
                var message = ""

                viewModel.sendFriendRequest(targetUser) { result, msg ->
                    success = result
                    message = msg
                }

                assertEquals(true, success)
                assertEquals("Friend request sent to ${targetUser.userName}!", message)
            }
        }
    }
}
