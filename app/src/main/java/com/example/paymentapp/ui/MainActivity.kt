package com.example.paymentapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import com.example.paymentapp.ui.pages.CreateGroup
import com.example.paymentapp.ui.pages.GroupManagement
import com.example.paymentapp.ui.pages.Groups
import com.example.paymentapp.ui.pages.SecondScreen
import com.example.paymentapp.ui.theme.PaymentAppTheme
import com.example.paymentapp.viewmodel.GroupViewModel
import androidx.compose.runtime.collectAsState

import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @SuppressLint("StringFormatInvalid")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied
            }
        }

        askNotificationPermission()

        FirebaseApp.initializeApp(this)

        if (FirebaseApp.getApps(this).isNotEmpty()) {
            Log.d("FirebaseInit", "Firebase initialized successfully")
        } else {
            Log.d("FirebaseInit", "Firebase initialization failed")
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            if (token != null) {
                Log.d(TAG, "FCM Token: $token")
                Toast.makeText(baseContext, "Token: $token", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "FCM Token is null")
            }
        }

        setContent {
            PaymentAppTheme {
                val navController = rememberNavController()
                val currentRoute = getCurrentRoute(navController)
                var currentGroupId by remember { mutableStateOf<String?>(null) }
                val viewModel: GroupViewModel = viewModel()

                Scaffold(
                    topBar = {
                        val group = currentGroupId?.let { groupId ->
                            viewModel.getGroupById(groupId)
                        }
                        AppBar(navController, currentRoute, "CashApp", group)
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "first",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("first") { Groups(navController, viewModel) }
                        composable("second") { SecondScreen(navController) }
                        composable("groupManagement/{groupId}") { backStackEntry ->
                            currentGroupId = backStackEntry.arguments?.getString("groupId")
                            val group = currentGroupId?.let { groupId ->
                                viewModel.getGroupById(groupId)
                            }
                            if (group != null) {
                                GroupManagement(viewModel, group)
                            }
                        }
                        composable("createGroup") {
                            CreateGroup(navController, viewModel)
                        }
                    }
                }
            }
        }
    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // For Android 13 and above, directly request the permission.
                // Consider showing a custom rationale UI here if necessary.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavHostController, currentRoute: String?, title: String, group: Group? = null) {
    CenterAlignedTopAppBar(
        title = {
            if (currentRoute == "groupManagement/{groupId}" && group != null) {
                Column {
                    Text(group.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = group.participants.joinToString(", ") { it.user.name },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                Text(title)
            }
        },
        navigationIcon = {
            if (currentRoute != "first") {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            }
        }
    )
}

@Composable
fun getCurrentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}