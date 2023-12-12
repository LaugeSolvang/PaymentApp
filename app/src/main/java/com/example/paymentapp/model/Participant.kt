package com.example.paymentapp.model

data class Participant(
    val user: User,  // Replacing 'name' with 'User' object
    val expenses: List<Expense>
)
