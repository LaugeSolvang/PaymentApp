package com.example.paymentapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.network.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow

class GroupRepository(private val localData: LocalData,
                      private val apiService: ApiService,
                      private val context: Context,
                      ) {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())

    suspend fun getGroupsForAccount(accountId: String): List<Group> {
        return try {
            // Fetch the user's account details to get the group IDs
            val account = apiService.getUser(accountId)
            // Map each group ID to its respective group details
            val groups = account.groupIds.mapNotNull { groupId ->
                try {
                    apiService.getGroup(groupId)
                } catch (e: Exception) {
                    // Log the error and return null to ignore errors for individual groups
                    Log.e("GroupRepository", "Error fetching group details for group ID: $groupId", e)
                    null
                }
            }
            // Save the retrieved groups locally
            localData.saveGroups(groups)
            groups
        } catch (e: Exception) {
            // Log the error and return an empty list in case of an error fetching the account
            Log.e("GroupRepository", "Error fetching groups for account: $accountId", e)
            emptyList()
        }
    }

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

    fun getGroup(groupId: String): Group {
        return localData.getGroupById(groupId)
    }



    suspend fun addExpense(groupId: String, userId: String, expense: Expense) {
        val groups = localData.getGroups()
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
    suspend fun createGroup(group: Group, accountId: String) {
        try {
            // Create the group and receive the created group details
            val createdGroup = apiService.createGroup(group)

            // Fetch the current account details
            val currentAccount = apiService.getUser(accountId)

            // Add the new group ID to the account's group IDs list
            val updatedGroupIds = currentAccount.groupIds + createdGroup.id
            val updatedAccount = currentAccount.copy(groupIds = updatedGroupIds)

            // Update the account with the new list of group IDs
            apiService.updateUser(accountId, updatedAccount)

            // Refresh the group data for the account
            _groups.value = getGroupsForAccount(accountId)
        } catch (e: Exception) {
            Log.e("GroupRepository", "Error creating group or updating account: ${e.message}")
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
    suspend fun removeParticipant(groupId: String, participantName: String) {
        val groups = localData.getGroups().toMutableList()
        val groupIndex = groups.indexOfFirst { it.id == groupId }

        if (groupIndex != -1) {
            val updatedGroup = groups[groupIndex].copy(
                participants = groups[groupIndex].participants.filterNot { it.user.name == participantName }
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
