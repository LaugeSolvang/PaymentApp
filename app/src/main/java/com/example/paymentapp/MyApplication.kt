package com.example.paymentapp

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Define your Group data class (structure according to your JSON)
data class Group(
    val id: String,
    val name: String,
    val balance: String,
    val participants: List<Participant>
)

data class Participant(
    val name: String,
    val expenses: List<Expense>
)

data class Expense(
    val description: String,
    val amount: String
)

// Retrofit service interface
interface GroupApiService {
    @GET("groups")
    suspend fun getGroups(): List<Group>
}

// ViewModel to manage groups
class GroupViewModel(application: Application) : AndroidViewModel(application) {
    var groups = mutableStateOf<List<Group>>(emptyList())

    private val apiService: GroupApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://172.17.212.100:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroupApiService::class.java)
    }

    init {
        viewModelScope.launch {
            try {
                val result = apiService.getGroups()
                groups.value = result
                Log.d("MyAppTag", "Groups loaded successfully")

            } catch (e: Exception) {
                Log.e("MyAppTag", "Error loading groups: ${e.message}")
            }
        }
    }
    fun getGroupById(groupId: String?): Group? {
        return groups.value.find { it.id == groupId }
    }

}

// Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
