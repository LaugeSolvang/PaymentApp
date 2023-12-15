package com.example.paymentapp.model
data class Group(
    val id: String,
    val name: String,
    val balance: String,
    val description: String,
    val participants: List<Participant>
)
