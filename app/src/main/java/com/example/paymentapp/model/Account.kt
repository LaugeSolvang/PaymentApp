package com.example.paymentapp.model

data class Account(
    val id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val groupIds: List<String> = emptyList() // Store only Group IDs
)