package com.example.paymentapp.model

data class Expense(
    val description: String,
    val amount: String,
    val shares: List<Share>
)
