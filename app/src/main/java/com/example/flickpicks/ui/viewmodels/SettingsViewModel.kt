package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickpicks.data.repository.UserSessionRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionRepository: UserSessionRepository,
    private val authManager: AuthManager
) : ViewModel() {

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            sessionRepository.clearSession()
            authManager.signOut()
            onComplete()
        }
    }
}

interface AuthManager {
    fun signOut()
}

class FirebaseAuthManager @Inject constructor() : AuthManager {
    override fun signOut() {
        Firebase.auth.signOut()
    }
}


