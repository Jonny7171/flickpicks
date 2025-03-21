package com.example.flickpicks.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.repository.PartyGroupFirestoreDatabase
import com.example.flickpicks.data.repository.PartyGroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyGroupViewModel @Inject constructor() : ViewModel() {
    val repository = PartyGroupRepository(PartyGroupFirestoreDatabase())
    private val _userPartyGroups = mutableStateListOf<PartyGroup>()
    val userPartyGroups: List<PartyGroup> get() = _userPartyGroups

    private val _messages =
        MutableStateFlow<List<ChatMessage>>(emptyList()) // Mutable Flow for Live Updates
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


    //fun sendMessage(message: ChatMessage) {
    //    messages.add(message)
    // }
    fun addPartyGroup(group: PartyGroup, userId: String) {
        viewModelScope.launch {
            val totalGroups = repository.getTotalPartyGroupsCount()


            val newGroup = group.copy(id = totalGroups + 1)


            repository.addPartyGroup(newGroup, userId)


            loadUserPartyGroups(userId)


        }
    }

    /*
    fun getPartyGroup(groupId: Int) {
        viewModelScope.launch {
            repository.getPartyGroup(groupId)
        }
    }

     */

    fun deletePartyGroup(group: PartyGroup) {

        viewModelScope.launch {
            repository.deletePartyGroup(group.id)
            _userPartyGroups.remove(group)
        }
    }

    /*
    fun updatePartyGroup(group: PartyGroup, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updatePartyGroup(group, updates)
        }
    }

     */

    fun sendMessage(groupId: Int, message: ChatMessage) {
        viewModelScope.launch {
            repository.sendChatMessage(groupId, message)
        }
    }


    /*
    fun getMessages(groupId: Int) {
        viewModelScope.launch {
            val newMessages = repository.getChatMessages(groupId)
            _messages.value = newMessages.sortedBy { it.timestamp } // Ensures messages are sorted
        }
    }

     */

    fun loadUserPartyGroups(userId: String) {

        viewModelScope.launch {
            _userPartyGroups.clear()
            _userPartyGroups.addAll(repository.getUserPartyGroups(userId))
        }
    }

    /*
    fun getTotalPartyCount() {
        viewModelScope.launch {
            repository.getTotalPartyGroupsCount()
        }
    }

     */

    fun findBestTime(groupId: Int, onResult: (String) -> Unit) {
        firestore.collection("party_groups")
            .document(groupId.toString())
            .get()
            .addOnSuccessListener { document ->
                val timesAvailable =
                    document.get("timesAvailable") as? Map<String, Map<String, List<String>>>
                        ?: return@addOnSuccessListener

                val dayFrequency = mutableMapOf<String, Int>()
                val timeFrequency = mutableMapOf<String, Int>()

                // Count votes for each day & time
                for (userAvailability in timesAvailable.values) {
                    userAvailability["days"]?.forEach { day ->
                        dayFrequency[day] = dayFrequency.getOrDefault(day, 0) + 1
                    }
                    userAvailability["times"]?.forEach { time ->
                        timeFrequency[time] = timeFrequency.getOrDefault(time, 0) + 1
                    }
                }

                // Find most common day & time
                val bestDay = dayFrequency.maxByOrNull { it.value }?.key ?: "No best day"
                val bestTime = timeFrequency.maxByOrNull { it.value }?.key ?: "No best time"

                onResult("$bestDay at $bestTime")
            }
            .addOnFailureListener {
                onResult("Error finding best time")
            }


    }
}





