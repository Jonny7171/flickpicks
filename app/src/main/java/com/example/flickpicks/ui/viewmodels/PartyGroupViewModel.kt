package com.example.flickpicks.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.ChatMessage
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.repository.PartyGroupFirestoreDatabase
import com.example.flickpicks.data.repository.PartyGroupRepository

import kotlinx.coroutines.launch

class PartyGroupViewModel: ViewModel() {
    val repository = PartyGroupRepository(PartyGroupFirestoreDatabase())

    val messages = mutableListOf(
        ChatMessage("Alice", "Hey, are we watching a movie tonight?", false),
        ChatMessage("You", "Yeah! What movie are you thinking?", true),
        ChatMessage("Bob", "Dune 2 maybe, but also we can look at movie recs.", false)
    )
    val selectedTimes = mutableStateListOf<String>()

    fun selectTime(time: String) {
        if (selectedTimes.contains(time)) {
            selectedTimes.remove(time)
        } else {
            selectedTimes.add(time)
        }
    }
    fun sendMessage(message: ChatMessage) {
        messages.add(message)
    }
    fun addPartyGroup(group: PartyGroup) {
        viewModelScope.launch {
            repository.addPartyGroup(group)
        }
    }
    fun getPartyGroup(group: PartyGroup) {
        viewModelScope.launch {
            repository.getPartyGroup(group.id)
        }
    }
    fun deletePartyGroup(group: PartyGroup) {
        viewModelScope.launch {
            repository.deletePartyGroup(group.id)
        }
    }
    fun updatePartyGroup(group: PartyGroup, updates: Map<String, Any>) {
        viewModelScope.launch {
            repository.updatePartyGroup(group, updates)
        }
    }
}


