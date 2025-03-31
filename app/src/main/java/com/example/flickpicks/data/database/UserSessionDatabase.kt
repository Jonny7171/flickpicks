package com.example.flickpicks.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Session::class], version = 1)
abstract class UserSessionDB : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: UserSessionDB? = null

        fun getInstance(context: Context): UserSessionDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserSessionDB::class.java,
                    "flickpicks_usersession_db"
                ).build().also { INSTANCE = it }
            }
        }
    }

}