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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.paymentapp.model.DebtItem

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

    fun calculateDebtSummary(groupId: String): Flow<List<DebtItem>> = flow {
        try {
        // Fetch the detailed group data
        val detailedGroup = groupRepository.getDetailedGroup(groupId)

        // Logic to calculate the balances...
        val balances = mutableMapOf<String, Double>()

        // Iterate over each participant in the group
        detailedGroup.participants.forEach { participant ->
            // Iterate over each expense of the participant
            participant.expenses.forEach { expense ->
                // Calculate the amount each participant owes for this expense
                val totalOwedPerParticipant = expense.amount.toDouble() / expense.shares.size
                expense.shares.forEach { share ->
                    val balance = balances.getOrDefault(share.user.id, 0.0)
                    balances[share.user.id] = balance - totalOwedPerParticipant
                }
                // Add the total expense amount to the balance of the participant who paid
                val payerBalance = balances.getOrDefault(participant.user.id, 0.0)
                balances[participant.user.id] = payerBalance + expense.amount.toDouble()
            }
        }

        // Convert balances to DebtItem list
        val debtSummary = balances.map { (userId, balance) ->
            val userName = users.value.find { it.id == userId }?.name ?: "Unknown"
            DebtItem(userName, if(balance >= 0) "+${balance.format()}€" else "${balance.format()}€")
        }

        Log.d("DebtCalculation", "Emitting debt summary: $debtSummary")
        emit(debtSummary)
        } catch (e: Exception) {
            Log.e("DebtCalculation", "Error calculating debt summary", e)
            emit(emptyList<DebtItem>()) // Emit an empty list in case of error
        }
    }

    fun Double.format(): String = String.format("%.2f", this)

    // Method to get participants of a group
    fun getGroupParticipants(groupId: String): List<Participant> {
        return groups.value.find { it.id == groupId }?.participants ?: emptyList()
    }

    // Method to get user ID by name
    fun getUserIdByName(name: String): String? {
        return users.value.find { it.name == name }?.id
    }
}

