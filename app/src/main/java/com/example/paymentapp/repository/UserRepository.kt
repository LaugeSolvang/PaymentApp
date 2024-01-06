package com.example.paymentapp.repository

import android.util.Log
import com.example.paymentapp.model.Account
import com.example.paymentapp.network.api.ApiService

class UserRepository(private val localData: LocalData, private val apiService: ApiService) {

    suspend fun createUser(user: Account) {
        try {
            // Save user to remote database through API
            apiService.createUser(user)
            // Optionally save user to local database
            localData.saveUser(user)
        } catch (e: Exception) {
            // Handle exceptions, such as network errors
            Log.e("UserRepository", "Error creating user: ${e.message}")
        }
    }

    suspend fun authenticateUser(email: String, password: String): Account? {
        return try {
            val users = apiService.getUsers()
            users.find { it.email == email && it.password == password }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error authenticating user: ${e.message}")
            null
        }
    }
    // Other user-related methods...
}
