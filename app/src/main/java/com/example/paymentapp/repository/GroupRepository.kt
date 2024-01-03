package com.example.paymentapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.network.api.GroupApiService
import com.example.paymentapp.viewmodel.GroupViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class GroupRepository(private val localData: LocalData,
                      private val apiService: GroupApiService,
                      private val context: Context,


) {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())

    suspend fun getGroups(): List<Group> {
        return if (isNetworkAvailable(context)) {
            try {
                val groups = apiService.getGroups()
                localData.saveGroups(groups)
                groups
            } catch (e: Exception) {
                localData.getGroups()
            }
        } else {
            localData.getGroups()
        }
    }

    suspend fun getUsers(): List<User> {
        return if (isNetworkAvailable(context)) {
            try {
                val users = apiService.getUsers()
                localData.saveUsers(users)
                users
            } catch (e: Exception) {
                localData.getUsers()
            }
        } else {
            localData.getUsers()
        }
    }

    suspend fun getDetailedGroup(groupId: String): Group {
        // First, get the basic group data
        val group = getGroups().find { it.id == groupId } ?: throw NoSuchElementException("Group not found")

        // Assuming each group already contains its participants and their expenses,
        // no additional data fetching is required here.
        // Just return the group as it is.
        return group
    }

    suspend fun addExpense(groupId: String, userId: String, expense: Expense) {
        val groups = localData.getGroups().toMutableList()
        val groupIndex = groups.indexOfFirst { it.id == groupId }

        if (groupIndex != -1) {
            val group = groups[groupIndex]
            val participantIndex = group.participants.indexOfFirst { it.user.id == userId }

            if (participantIndex != -1) {
                // Add the expense to the participant
                val updatedParticipant = group.participants[participantIndex].copy(
                    expenses = group.participants[participantIndex].expenses + expense
                )
                // Update the group with the new participant info
                val updatedGroup = group.copy(
                    participants = group.participants.toMutableList().apply {
                        set(participantIndex, updatedParticipant)
                    }
                )
                // Save the updated group locally and try to update it on the server
                updateGroupLocally(updatedGroup)
                try {
                    apiService.updateGroup(groupId, updatedGroup)
                } catch (e: Exception) {
                    // Handle error, perhaps by rethrowing or logging
                }
            }
        }
    }

    private fun updateGroupLocally(updatedGroup: Group) {
        val currentGroups = localData.getGroups().toMutableList()
        val index = currentGroups.indexOfFirst { it.id == updatedGroup.id }
        if (index != -1) {
            currentGroups[index] = updatedGroup
            localData.saveGroups(currentGroups)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
    suspend fun createGroup(group: Group) {
        try {
            // Call the appropriate method in your repository to add the new group
            apiService.createGroup(group)
            // Optionally, refresh group data if needed
            _groups.value = getGroups()
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error creating group: ${e.message}")
        }
    }
    suspend fun addParticipant(groupId: String, participant: Participant) {
        val groups = localData.getGroups().toMutableList()
        val groupIndex = groups.indexOfFirst { it.id == groupId }

        if (groupIndex != -1) {
            val updatedGroup = groups[groupIndex].copy(
                participants = groups[groupIndex].participants + participant
            )
            updateGroupLocally(updatedGroup)
            try {
                apiService.updateGroup(groupId, updatedGroup)
            } catch (e: Exception) {
                // Handle error, perhaps by rethrowing or logging
            }
        }
    }
}
