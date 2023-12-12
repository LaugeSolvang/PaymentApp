package com.example.paymentapp.ui.pages

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun SecondScreen(navController: NavHostController) {
    Button(onClick = { navController.navigate("first") }) {
        Text("Go to First Screen")
    }
}
