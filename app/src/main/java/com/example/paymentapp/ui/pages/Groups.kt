package com.example.paymentapp.ui.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.paymentapp.data.LoginViewModel
import com.example.paymentapp.model.Group
import com.example.paymentapp.viewmodel.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Groups(navController: NavHostController, viewModel: GroupViewModel, loginViewModel: LoginViewModel) {
    loginViewModel.accountId.value?.let { Log.d("Value", it) }
    val accountId = loginViewModel.accountId.value

    if (accountId != null) {
        viewModel.setCurrentAccountId(accountId)
    }
    val groups by viewModel.groups.collectAsState()

    Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    // Navigate to the new destination for creating a group
                    navController.navigate("createGroup")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Group")
                }
            }
    ) { innerPadding ->
        GroupList(groups = groups, navController, modifier = Modifier.padding(innerPadding))
    }
}


@Composable
fun GroupList(groups: List<Group>, navController: NavHostController, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(groups) { group ->
            GroupItem(group, navController)
        }
    }
}

@Composable
fun GroupItem(group: Group, navController: NavHostController) {
    Surface(
        modifier = Modifier
            .clickable { navController.navigate("groupManagement/${group.id}") }
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.padding(start = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = group.balance,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}