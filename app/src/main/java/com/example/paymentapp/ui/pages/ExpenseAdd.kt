package com.example.paymentapp.ui.pages

import FCMHelper
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.paymentapp.R
import com.example.paymentapp.viewmodel.GroupViewModel
import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Share
import com.example.paymentapp.network.RetrofitBuilder
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseAdd(navController: NavController, viewModel: GroupViewModel, groupId: String) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var splitEqually by remember { mutableStateOf(false) }
    val participants = viewModel.getGroupParticipants(groupId).map { it.user.name }
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf(participants.firstOrNull() ?: "") }

    val groups by viewModel.groups.collectAsState(emptyList())
    val group = groups.find { it.id == groupId }

    // Map to store the owed amounts for each participant
    var owedAmounts by remember { mutableStateOf(participants.associateWith { "" }) }

    val fcmHelper = FCMHelper()

    LaunchedEffect(splitEqually, amount) {
        if (splitEqually && amount.toDoubleOrNull() != null) {
            val equalShare = amount.toDouble() / participants.size
            owedAmounts = participants.associateWith { equalShare.toString() }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = selectedUser,
                onValueChange = { selectedUser = it },
                label = { Text("Paid by") },
                readOnly = true, // make the text field read-only
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, "Drop-down icon", Modifier.clickable { expanded = !expanded })
                },
                modifier = Modifier.fillMaxWidth()
            )
            Box {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    participants.forEach { participant ->
                        DropdownMenuItem(
                            text = { Text(participant) },
                            onClick = {
                                selectedUser = participant
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = splitEqually,
                    onCheckedChange = { splitEqually = it }
                )
                Text("Split Equally")
            }

            if (!splitEqually) {
                Text("Specify Shares:")
                participants.forEach { participant ->
                    Row {
                        TextField(
                            value = participant,
                            onValueChange = {},
                            label = { Text("User Name") },
                            readOnly = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TextField(
                            value = owedAmounts[participant] ?: "",
                            onValueChange = { owedAmounts = owedAmounts.toMutableMap().apply { put(participant, it) } },
                            label = { Text("Owed") },
                            modifier = Modifier.fillMaxWidth(2f)
                        )
                    }
                }            }
            Spacer(modifier = Modifier.height(16.dp))
            val currentContext = LocalContext.current
            Button(onClick = {
                val totalAmount = amount.toDoubleOrNull() ?: 0.0

                val expenseShares: List<Share> = if (splitEqually) {
                    Log.d("ShareMoney", "Splitting equally")

                    val participants = viewModel.getGroupParticipants(groupId)
                    val equalShare = totalAmount / participants.size

                    participants.map { participant ->
                        Share(user = participant.user, owed = String.format("%.2f", equalShare))
                    }
                } else {
                    Log.d("ShareMoney", "Using owed amounts")

                    owedAmounts.mapNotNull { (name, owedAmount) ->
                        Log.d("Debug", "Processing share for $name with amount $owedAmount")

                        viewModel.getGroupParticipants(groupId).find { it.user.name == name }?.let { participant ->
                            Share(user = participant.user, owed = owedAmount).also {
                                Log.d("Debug", "Created Share: $it")
                            }
                        }
                    }
                }
                val newExpense = Expense(description = description, amount = amount, shares = expenseShares)
                RetrofitBuilder.clearCache()
                val payerId = group?.participants?.find { it.user.name == selectedUser }?.user?.id
                if (payerId != null) {
                    viewModel.addExpense(groupId, payerId, newExpense)
                } else {
                    Log.e("ExpenseAdd", "Failed to find user ID for selected user: $selectedUser")
                }
                navController.navigateUp()


                val tokens = listOf("d8MJY7rtTmu4PfTplYyBqx:APA91bFfBw3yUeJvO183fbinZ2c9nf61jzvHCpGLJyCN4aOqrDpBw-J_Wm-dwZYjh6tu5KTjsVOe0MHc77VeKFL7cryjsvGbn-EN2s9I3j6GmEjJAgN6x7ddS4-Qpr5ErTxTgRGshCja", ) //Feel free to add your FCM tokens here
                val title = "Title" //The title of the notification is defined in FirebaseMessagingService
                val message = currentContext.getString(R.string.msg_context_text)
                fcmHelper.sendNotification(tokens, title, message)
            }) {
                Text("Submit")
            }
        }
    }
}
