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
import com.example.paymentapp.network.api.ApiService
import com.example.paymentapp.repository.GroupRepository
import com.example.paymentapp.repository.LocalData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import com.example.paymentapp.model.DebtItem
import kotlinx.coroutines.flow.map

class GroupViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitBuilder.getGroupApiService(application)
    private val localData = LocalData(application)
    private val groupRepository = GroupRepository(localData, apiService, application)

    // MutableStateFlow for internal updates
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    private val _users = MutableStateFlow<List<User>>(emptyList())

    private var currentAccountId: String = "" // Initialize with empty string or default value

    // Exposed as read-only StateFlow
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // Method to update current account ID
    fun setCurrentAccountId(accountId: String) {
        Log.d("HI", "Anything happens here")
        currentAccountId = accountId
        loadGroupsForAccount(accountId)
    }


    private fun loadGroupsForAccount(accountId: String) {
        viewModelScope.launch {
            try {
                val groups = groupRepository.getGroupsForAccount(accountId)
                _groups.value = groups
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error loading groups for account: ${e.message}")
            }
        }
    }

    fun getGroupById(groupId: String?): Flow<Group?> {
        return groups.map { groupList ->
            groupList.find { it.id == groupId }
        }
    }

    fun addExpense(groupId: String, userId: String, expense: Expense) {
        viewModelScope.launch {
            try {
                groupRepository.addExpense(groupId, userId, expense)
                // Optionally, refresh group data if needed
                _groups.value = groupRepository.getGroupsForAccount(currentAccountId)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error adding expense: ${e.message}")
            }
        }
    }

    fun calculateDebtSummary(groupId: String): Flow<List<DebtItem>> = flow {
        try {
            // Trying hardcoded data
            // emit(listOf(DebtItem("Test User", "+10€")))

            val detailedGroup = groupRepository.getGroup(groupId)

        // Initialize a map to track balances for each user
        val balances = detailedGroup.participants.associate { it.user.id to 0.0 }.toMutableMap()

        // Calculate expenses and update balances
        detailedGroup.participants.forEach { participant ->
            participant.expenses.forEach { expense ->
                val amount = expense.amount.toDouble()
                val payerId = participant.user.id
                val numberOfShares = expense.shares.size

                // Subtract the shared amount from each user except the payer
                expense.shares.forEach { share ->
                    val shareAmount = amount / numberOfShares
                    if (share.user.id != payerId) {
                        balances[share.user.id] = balances[share.user.id]!! - shareAmount
                        balances[payerId] = balances[payerId]!! + shareAmount
                    }
                }
            }
        }
            Log.d("GroupViewModel", users.value.toString())
        // Convert balances to DebtItem list
        val debtSummary = balances.map { (userId, balance) ->
            val userName = detailedGroup.participants.find { it.user.id == userId }?.user?.name ?: "Unknown"
            DebtItem(userName, if (balance >= 0) "+${"%.2f".format(balance)}€" else "${"%.2f".format(balance)}€")
        }

            debtSummary.forEach { debtItem ->
                Log.d("GroupViewModel", "DebtItem - Name: ${debtItem.name}, Balance: ${debtItem.balance}")
            }

            emit(debtSummary)
        } catch (e: Exception) {
            Log.e("DebtCalculation", "Error calculating debt summary", e)
            emit(emptyList<DebtItem>()) // Emit an empty list in case of error
        }
    }

    // Method to get participants of a group
    fun getGroupParticipants(groupId: String): List<Participant> {
        return groups.value.find { it.id == groupId }?.participants ?: emptyList()
    }

    fun createGroup(group: Group) {
        viewModelScope.launch {
            try {
                Log.d("GroupViewModel", "Creating group: $group")
                groupRepository.createGroup(group, currentAccountId)
                _groups.value = groupRepository.getGroupsForAccount(currentAccountId)
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error creating group: ${e.message}")
            }
        }
    }
    fun addParticipant(groupId: String, participant: Participant) {
        viewModelScope.launch {
            try {
                groupRepository.addParticipant(groupId, participant)
                // Optionally, refresh group data if needed
                _groups.value = groupRepository.getGroups()
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error adding participant: ${e.message}")
            }
        }
    }
    fun removeParticipant(groupId: String, userId: String) {
        viewModelScope.launch {
            try {
                groupRepository.removeParticipant(groupId, userId)
                // Optionally, refresh group data if needed
                _groups.value = groupRepository.getGroups()
            } catch (e: Exception) {
                Log.e("GroupViewModel", "Error removing participant: ${e.message}")
            }
        }
    }
}

