package com.example.flickpicks.data.database

import androidx.room.*


@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSession(session: Session)

    @Query("SELECT * FROM UserSessions LIMIT 1")
    suspend fun getSession(): Session?

    @Query("DELETE FROM UserSessions")
    suspend fun clearSession()
}