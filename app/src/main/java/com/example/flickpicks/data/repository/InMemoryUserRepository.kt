package com.example.flickpicks.data.repository

import com.example.flickpicks.data.model.UserProfile

class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf<UserProfile>()

    override fun createUser(userProfile: UserProfile) {
        users.add(userProfile)
    }

    override fun getUser(userName: String): UserProfile? {
        return users.find { it.userName == userName }
    }
}
