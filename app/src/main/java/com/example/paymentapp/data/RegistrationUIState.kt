package com.example.paymentapp.data

data class RegistrationUIState (
    var firstName :String = "",
    var lastName :String = "",
    var email :String = "",
    var password :String = "",

    var firstNameError: Boolean = false,
    var lastNameError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false
){
    fun hasErrors(): Boolean {
        return firstNameError || lastNameError || emailError || passwordError
    }
}

