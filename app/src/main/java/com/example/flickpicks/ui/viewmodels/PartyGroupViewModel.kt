package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.model.PartyGroup
import com.example.flickpicks.data.repository.PartyGroupFirestoreDatabase
import com.example.flickpicks.data.repository.PartyGroupRepository

import kotlinx.coroutines.launch

class PartyGroupViewModel: ViewModel() {
    val repository = PartyGroupRepository(PartyGroupFirestoreDatabase())
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


