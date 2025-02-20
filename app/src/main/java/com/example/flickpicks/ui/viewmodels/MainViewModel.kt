package com.example.flickpicks.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.flickpicks.data.model.UserProfile

/**
 * Temporary in-memory repository to hold user data.
 * We will replace this with the Database when that is added in
 */
class InMemoryUserRepository {
    private val users = mutableListOf<UserProfile>()

    fun createUser(userProfile: UserProfile) {
        users.add(userProfile)
    }

    fun getUser(userName: String): UserProfile? {
        return users.find { it.userName == userName }
    }
}

class MainViewModel : ViewModel() {

    private val userRepository = InMemoryUserRepository()

    // Variable to hold the currently signed-in user
    private var currentUser: UserProfile? = null

    // ID generator for now.
    private var currentId = 1
    private fun generateUserId(): Int = currentId++

    /**
     * Sign up a user by creating a new UserProfile in the in-memory repository.
     * Also sets the newly created user as the current user.
     */
    fun signUpUser(
        name: String,
        userName: String,
        password: String,
        email: String,
        phoneNumber: String,
        profilePicUrl: String? = null
    ) {
        val newUser = UserProfile(
            id = generateUserId(),
            name = name,
            userName = userName,
            password = password,
            email = email,
            phoneNumber = phoneNumber,
            profilePicUrl = profilePicUrl
        )
        userRepository.createUser(newUser)
        currentUser = newUser  // Set the current user after sign-up
    }

    /**
     * Retrieve the currently signed-in user.
     */
    fun getCurrentUser(): UserProfile? = currentUser

}
