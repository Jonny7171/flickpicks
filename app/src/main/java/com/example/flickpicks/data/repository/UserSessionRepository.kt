package com.example.flickpicks.data.repository
import com.example.flickpicks.data.database.Session
import com.example.flickpicks.data.database.SessionDao

import javax.inject.Inject

class UserSessionRepository @Inject constructor(
    private val dao: SessionDao
) {
    suspend fun saveSession(userId: String, email: String) {
        dao.saveSession(Session(userId, email))
    }

    suspend fun getSession(): Session? {
        return dao.getSession()
    }

    suspend fun clearSession() {
        dao.clearSession()
    }

}