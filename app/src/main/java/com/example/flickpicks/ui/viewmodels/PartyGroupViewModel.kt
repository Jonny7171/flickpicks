package com.example.flickpicks.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.repository.MoviesRepository
import com.example.flickpicks.data.repository.PartyGroupFirestoreDatabase
import com.example.flickpicks.data.repository.PartyGroupRepository
import com.example.flickpicks.data.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyGroupViewModel @Inject constructor(
    val userRepository: UserProfileRepository,
    val moviesRepository: MoviesRepository
) : ViewModel() {
    val repository = PartyGroupRepository(PartyGroupFirestoreDatabase())
    private val _userPartyGroups = mutableStateListOf<PartyGroup>()
    val userPartyGroups: List<PartyGroup> get() = _userPartyGroups

    private val _messages =
        MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _currentUserName = MutableStateFlow<String?>(null)

    private val _selectedDays = MutableStateFlow<List<String>>(emptyList())
    val selectedDays: StateFlow<List<String>> = _selectedDays.asStateFlow()

    private val _selectedTimes = MutableStateFlow<List<String>>(emptyList())
    val selectedTimes: StateFlow<List<String>> = _selectedTimes.asStateFlow()


    private val _partyGroup = MutableStateFlow<PartyGroup?>(null)
    val partyGroup: StateFlow<PartyGroup?> = _partyGroup.asStateFlow()

    fun loadMessages(groupId: Int) {
        viewModelScope.launch {
            repository.listenForChatMessages(groupId) { newMessages ->
                _messages.value = newMessages.sortedBy { it.timestamp }
            }
        }
    }

    fun loadUserAvailability(groupId: Int) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("party_groups")
            .document(groupId.toString())
            .get()
            .addOnSuccessListener { document ->
                val userAvailability = document.get("timesAvailable.$userId") as? Map<String, List<String>>
                _selectedDays.value = userAvailability?.get("days") ?: emptyList()
                _selectedTimes.value = userAvailability?.get("times") ?: emptyList()
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failed to load user availability")
            }
    }

    fun loadPartyGroup(groupId: Int) {
        viewModelScope.launch {
            val group = repository.getPartyGroup(groupId)
            _partyGroup.value = group
            loadUserAvailability(groupId)
        }
    }
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    fun loadCurrentUserName() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                _currentUserName.value = document.getString("userName") ?: "Unknown"
            }
            .addOnFailureListener {
                _currentUserName.value = "Unknown"
            }
    }

    fun toggleDaySelection(groupId: Int, day: String) {
        val userId = auth.currentUser?.uid ?: return
        val currentSelections = _selectedDays.value.toMutableList()

        if (currentSelections.contains(day)) {
            currentSelections.remove(day)
        } else {
            currentSelections.add(day)
        }

        _selectedDays.value = currentSelections


        firestore.collection("party_groups")
            .document(groupId.toString())
            .update("timesAvailable.$userId.days", currentSelections)
            .addOnSuccessListener { Log.d("Firestore", "Day availability updated") }
            .addOnFailureListener { Log.e("Firestore", "Failed to update days") }


    }

    fun toggleTimeSelection(groupId: Int, time: String) {
        val userId = auth.currentUser?.uid ?: return
        val currentSelections = _selectedTimes.value.toMutableList()

        if (currentSelections.contains(time)) {
            currentSelections.remove(time)
        } else {
            currentSelections.add(time)
        }

        _selectedTimes.value = currentSelections


        firestore.collection("party_groups")
            .document(groupId.toString())
            .update("timesAvailable.$userId.times", currentSelections)
            .addOnSuccessListener { Log.d("Firestore", "Time availability updated") }
            .addOnFailureListener { Log.e("Firestore", "Failed to update times") }


    }

    fun addPartyGroup(group: PartyGroup, userId: String) {
        viewModelScope.launch {
            val totalGroups = repository.getTotalPartyGroupsCount()


            val newGroup = group.copy(id = totalGroups + 1)


            repository.addPartyGroup(newGroup, userId)


            loadUserPartyGroups(userId)


        }
    }

    fun deletePartyGroup(group: PartyGroup) {

        viewModelScope.launch {
            repository.deletePartyGroup(group.id)
            _userPartyGroups.remove(group)
        }
    }


    fun sendMessage(groupId: Int, message: ChatMessage) {
        viewModelScope.launch {
            repository.sendChatMessage(groupId, message)
        }
    }



    fun loadUserPartyGroups(userId: String) {

        viewModelScope.launch {
            _userPartyGroups.clear()
            _userPartyGroups.addAll(repository.getUserPartyGroups(userId))
        }
    }


    fun findBestTime(groupId: Int, onResult: (String) -> Unit) {
        firestore.collection("party_groups")
            .document(groupId.toString())
            .get()
            .addOnSuccessListener { document ->
                val timesAvailable =
                    document.get("timesAvailable") as? Map<String, Map<String, List<String>>>
                        ?: return@addOnSuccessListener

                val allDays = mutableListOf<Set<String>>()
                val allTimes = mutableListOf<Set<String>>()
                for (userAvailability in timesAvailable.values) {
                    val userDays = userAvailability["days"]?.toSet() ?: emptySet()
                    val userTimes = userAvailability["times"]?.toSet() ?: emptySet()

                    if (userDays.isNotEmpty()) allDays.add(userDays)
                    if (userTimes.isNotEmpty()) allTimes.add(userTimes)
                }

                if (allDays.isEmpty() || allTimes.isEmpty()) {
                    onResult("You haven't selected your availability")
                    return@addOnSuccessListener
                }

                val commonDays = allDays.reduce { acc, set -> acc.intersect(set) }
                val commonTimes = allTimes.reduce { acc, set -> acc.intersect(set) }

                if (commonDays.isEmpty() || commonTimes.isEmpty()) {
                    onResult("No common times available")
                    return@addOnSuccessListener
                }

                val bestDay = commonDays.first()
                val bestTime = commonTimes.first()

                onResult("$bestDay at $bestTime")


            }
            .addOnFailureListener {
                onResult("Error finding best time")
            }


    }

     fun voteOnMovie(id: Int, userId: String, movieId: String, vote: Boolean) {
         viewModelScope.launch {
             repository.voteOnMovie(id, userId, movieId, vote)
         }
    }
    fun startNewGame(groupId: Int, userId: String) {
        viewModelScope.launch {
            repository.startNewGame(
                id = groupId,
                getUserProfile = { userId -> userRepository.getUserProfile(userId) },
                fetchMoviesByGenre = { genreList -> moviesRepository.getMoviesByGenres(genreList) }
            )
            loadPartyGroup(groupId)
        }
    }

    fun resumeExistingGame(groupId: Int, userId: String) {
        viewModelScope.launch {
            repository.playExistingGame(groupId, userId)
            loadPartyGroup(groupId)
        }
    }

}





