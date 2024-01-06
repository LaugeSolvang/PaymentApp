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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.paymentapp.ui.pages.CreateGroup
import com.example.paymentapp.ui.pages.GroupManagement
import com.example.paymentapp.ui.pages.Groups
import com.example.paymentapp.ui.pages.SecondScreen
import com.example.paymentapp.ui.theme.PaymentAppTheme
import com.example.paymentapp.viewmodel.GroupViewModel
import androidx.compose.runtime.collectAsState
import com.example.paymentapp.data.LoginViewModel
import com.example.paymentapp.ui.pages.LoginScreen
import com.example.paymentapp.ui.pages.SignUpScreen

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
                val loginViewModel: LoginViewModel = viewModel()

                Scaffold(
                    topBar = {
                        AppBar(navController, currentRoute, "CashApp", viewModel, currentGroupId)
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "signup",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("signup") { SignUpScreen(navController, loginViewModel) } // Add SignUpScreen route
                        composable("login") { LoginScreen(navController, loginViewModel) } // Add SignUpScreen route
                        composable("first") { Groups(navController, viewModel, loginViewModel) }
                        composable("second") { SecondScreen(navController) }
                        composable("groupManagement/{groupId}") { backStackEntry ->
                            currentGroupId = backStackEntry.arguments?.getString("groupId")
                            val group = currentGroupId?.let { groupId ->
                                viewModel.getGroupById(groupId)
                            }
                            if (group != null) {
                                currentGroupId?.let { GroupManagement(viewModel, it) }
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
fun AppBar(navController: NavHostController, currentRoute: String?, title: String, viewModel: GroupViewModel, groupId: String?) {
    val group by viewModel.getGroupById(groupId).collectAsState(initial = null)

    CenterAlignedTopAppBar(
        title = {
            if (currentRoute == "groupManagement/{groupId}" && group != null) {
                Column {
                    Text(group!!.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = group!!.participants.joinToString(", ") { it.user.name },
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