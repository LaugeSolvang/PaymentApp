package com.example.paymentapp.data

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentapp.data.rules.Validator
import com.example.paymentapp.model.Account
import com.example.paymentapp.network.RetrofitBuilder
import com.example.paymentapp.network.api.ApiService
import com.example.paymentapp.repository.LocalData
import com.example.paymentapp.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.UUID

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitBuilder.getGroupApiService(application)
    private val localData = LocalData(application)
    private val userRepository = UserRepository(localData, apiService)
    var accountId = mutableStateOf<String?>(null)

    private val TAG = LoginViewModel::class.simpleName

    var registrationUIState = mutableStateOf(RegistrationUIState())

    var account = mutableStateOf<Account?>(null)

    fun onEvent(event:UIEvent){
        validateDataWithRules()

        when(event){
            is UIEvent.FirstNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    firstName = event.firstName
                )
                printState()
            }

            is UIEvent.LastNameChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    lastName = event.lastName
                )
                printState()
            }

            is UIEvent.EmailChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    email = event.email
                )
                printState()
            }

            is UIEvent.PasswordChanged -> {
                registrationUIState.value = registrationUIState.value.copy(
                    password = event.password
                )
                printState()
            }

            is UIEvent.RegisterButtonClicked -> {
                signUp()
            }

            is UIEvent.LoginButtonClicked -> {
                login()
            }

        }
    }

    private fun signUp() {
        Log.d(TAG, "Inside_signUp")
        // ...


        // Assuming you have a valid User object from the registration UI state
        val newUser = Account(
            id = UUID.randomUUID().toString(),
            firstname = registrationUIState.value.firstName,
            lastname = registrationUIState.value.lastName,
            email = registrationUIState.value.email,
            password = registrationUIState.value.password,
            groupIds = listOf() // Initialize with an empty list
        )

        viewModelScope.launch {
            try {
                userRepository.createUser(newUser)
                // Handle post-registration logic
                accountId.value = newUser.id
                getUserDetails() // Fetch the user details after successful sign up
                Log.d("LoginViewModel", "Account created with ID: ${accountId.value}")

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error creating user: ${e.message}")
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Login successful with account ID: ${accountId.value}")

                // Assuming you have a method in your UserRepository to authenticate the user
                val user = userRepository.authenticateUser(
                    email = registrationUIState.value.email,
                    password = registrationUIState.value.password
                )
                if (user != null) {
                    accountId.value = user.id
                    getUserDetails() // Fetch user details after successful login
                    Log.d(TAG, "Login successful with account ID: ${accountId.value}")
                } else {
                    Log.e(TAG, "Login failed: User not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during login: ${e.message}")
            }
        }
    }
    fun getUserDetails() {
        viewModelScope.launch {
            accountId.value?.let { userId ->
                userRepository.getUserDetails(userId)?.let { user ->
                    account.value = user
                    Log.d(TAG, "Fetched user details: $user")
                }
            }
        }
    }

    fun updateUserDetails(firstName: String, lastName: String, email: String) {
        account.value?.let { existingUser ->
            val updatedUser = Account(
                id = existingUser.id,
                firstname = firstName,
                lastname = lastName,
                email = email,
                password = existingUser.password, // Assuming password is part of the existing account
                groupIds = existingUser.groupIds // Preserve the existing groupIds
            )
            Log.d(TAG, "Updating user details with: $updatedUser")

            viewModelScope.launch {
                userRepository.updateUserDetails(updatedUser.id, updatedUser)
            }
        }
    }

    fun updateUserDetails() {
        account.value?.let { updatedUser ->
            Log.d(TAG, "Updating user details with: $updatedUser")
            viewModelScope.launch {
                userRepository.updateUserDetails(updatedUser.id, updatedUser)
            }
        }
    }


    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(
            fName = registrationUIState.value.firstName
        )

        val lNameResult = Validator.validateLastName(
            lName = registrationUIState.value.lastName
        )

        val emailResult = Validator.validateEmail(
            email = registrationUIState.value.email
        )

        val passwordResult = Validator.validatePassword(
            password = registrationUIState.value.password
        )

        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult= $fNameResult")
        Log.d(TAG, "lNameResult= $lNameResult")
        Log.d(TAG, "emailResult= $emailResult")
        Log.d(TAG, "passwordResult= $passwordResult")

        registrationUIState.value = registrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )


    }

    private fun printState(){
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, registrationUIState.value.toString())
    }

}