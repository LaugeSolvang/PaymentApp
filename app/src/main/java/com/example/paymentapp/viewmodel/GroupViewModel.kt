package com.example.paymentapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.network.RetrofitBuilder
import com.example.paymentapp.network.api.GroupApiService
import com.example.paymentapp.repository.GroupRepository
import com.example.paymentapp.repository.LocalData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: GroupApiService = RetrofitBuilder.getGroupApiService(application)
    private val localData = LocalData(application)
    private val groupRepository = GroupRepository(localData, apiService, application)

    // MutableStateFlow for internal updates
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    private val _users = MutableStateFlow<List<User>>(emptyList())

    // Exposed as read-only StateFlow
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        loadGroups()
        loadUsers()
    }

    fun loadGroups() {
        viewModelScope.launch {
            try {
                val result = groupRepository.getGroups()
                _groups.value = result
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error loading groups: ${e.message}")
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val result = groupRepository.getUsers()
                _users.value = result
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error loading users: ${e.message}")
            }
        }
    }

    private suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            try {
                apiService.getUsers()
            } catch (e: Exception) {
                Log.e("MyAppTag", "Error loading users: ${e.message}")
                emptyList()
            }
        }
    }

    fun getGroupById(groupId: String?): Group? {
        return groups.value.find { it.id == groupId }
    }

    fun addExpense(groupId: String, userId: String, expense: Expense) {
        viewModelScope.launch {
            try {
                groupRepository.addExpense(groupId, userId, expense)
                // Optionally, refresh group data if needed
                _groups.value = groupRepository.getGroups()
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error adding expense: ${e.message}")
            }
        }
    }

    // Method to get participants of a group
    fun getGroupParticipants(groupId: String): List<Participant> {
        return groups.value.find { it.id == groupId }?.participants ?: emptyList()
    }

    // Method to get user ID by name
    fun getUserIdByName(name: String): String? {
        return users.value.find { it.name == name }?.id
    }
}

