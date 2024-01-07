package com.example.paymentapp.ui.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.paymentapp.data.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(viewModel: LoginViewModel) {
    val account = viewModel.account.value // Directly access the value of MutableState
    if (account != null) {
        Log.d("Account", account.firstname)
    } else {
        Log.d("No account","No account")
    }

    account?.let {
        var firstName by remember { mutableStateOf(it.firstname) }
        var lastName by remember { mutableStateOf(it.lastname) }
        var email by remember { mutableStateOf(it.email) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.account.value = viewModel.account.value?.copy(
                        firstname = firstName,
                        lastname = lastName,
                        email = email,
                        groupIds = viewModel.account.value?.groupIds ?: emptyList() // Preserve existing groupIds
                    )
                    viewModel.updateUserDetails(firstName, lastName, email)
                }
            ) {
                Text("Save Changes")
            }
        }
    }
}
