package com.example.paymentapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.network.RetrofitBuilder
import com.example.paymentapp.network.api.GroupApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(application: Application) : AndroidViewModel(application) {
    var groups = mutableStateOf<List<Group>>(emptyList())
    var users = mutableStateOf<List<User>>(emptyList())

    private val apiService: GroupApiService = RetrofitBuilder.groupApiService

    init {
        loadGroups()
        loadUsers()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            try {
                val result = apiService.getGroups()
                groups.value = result
            } catch (e: Exception) {
                Log.e("MyAppTag", "Error loading groups: ${e.message}")
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            users.value = getAllUsers()
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
        viewModelScope.launch(Dispatchers.IO) {
            val groupIndex = groups.value.indexOfFirst { it.id == groupId }
            if (groupIndex != -1) {
                val group = groups.value[groupIndex]
                val participantIndex = group.participants.indexOfFirst { it.user.id == userId }
                if (participantIndex != -1) {
                    val updatedParticipant = group.participants[participantIndex].copy(
                        expenses = group.participants[participantIndex].expenses + expense
                    )
                    val updatedGroup = group.copy(
                        participants = group.participants.toMutableList().apply {
                            set(participantIndex, updatedParticipant)
                        }
                    )

                    // Update the group data on the server and locally
                    try {
                        apiService.updateGroup(groupId, updatedGroup)
                        // Update local data
                        updateGroupLocally(groupIndex, updatedGroup)
                    } catch (e: Exception) {
                        Log.e("MyAppTag", "Error updating group: ${e.message}")
                    }
                }
            }
        }
    }
    private fun updateGroupLocally(index: Int, updatedGroup: Group) {
        val updatedGroups = groups.value.toMutableList().apply {
            set(index, updatedGroup)
        }
        groups.value = updatedGroups
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

