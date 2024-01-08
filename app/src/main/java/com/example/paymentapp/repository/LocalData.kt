package com.example.paymentapp.repository

import android.content.Context
import com.example.paymentapp.model.Account
import com.example.paymentapp.model.Expense
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.User

class LocalData(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("LocalCache", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveGroups(groups: List<Group>) {
        val json = gson.toJson(groups)
        sharedPreferences.edit().putString("groups", json).apply()
    }

    fun getGroups(): List<Group> {
        val json = sharedPreferences.getString("groups", null)
        return if (json != null) {
            val type = object : TypeToken<List<Group>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getGroupById(groupId: String): Group {
        val groups = getGroups()
        return groups.find { it.id == groupId }
            ?: throw NoSuchElementException("Group not found with ID: $groupId")
    }

    fun saveUser(user: Account) {
        val json = gson.toJson(user)
        sharedPreferences.edit().putString("user", json).apply()
    }

    fun saveExpenses(expenses: List<Expense>) {
        val json = gson.toJson(expenses)
        sharedPreferences.edit().putString("expenses", json).apply()
    }

    fun getExpenses(): List<Expense> {
        val json = sharedPreferences.getString("expenses", null)
        return if (json != null) {
            val type = object : TypeToken<List<Expense>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
