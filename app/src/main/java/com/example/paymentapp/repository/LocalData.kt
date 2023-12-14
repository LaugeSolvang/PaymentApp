package com.example.paymentapp.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.User

class LocalData(private val context: Context) {

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

    fun saveUsers(users: List<User>) {
        val json = gson.toJson(users)
        sharedPreferences.edit().putString("users", json).apply()
    }

    fun getUsers(): List<User> {
        val json = sharedPreferences.getString("users", null)
        return if (json != null) {
            val type = object : TypeToken<List<User>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
