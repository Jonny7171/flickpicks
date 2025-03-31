package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.database.Session
import com.example.flickpicks.data.repository.UserSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    fun saveSession(userId: String, email: String) {
        viewModelScope.launch {
            sessionRepository.saveSession(userId, email)
        }
    }
}