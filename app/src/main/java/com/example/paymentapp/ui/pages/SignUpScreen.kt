package com.example.paymentapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.paymentapp.R
import com.example.paymentapp.components.ButtonComponent
import com.example.paymentapp.components.ClickableLoginTextComponent
import com.example.paymentapp.components.DividerTextComponent
import com.example.paymentapp.components.HeadingTextComponent
import com.example.paymentapp.components.MyTextFieldComponent
import com.example.paymentapp.components.NormalTextComponent
import com.example.paymentapp.components.PasswordTextFieldComponent
import com.example.paymentapp.data.LoginViewModel
import com.example.paymentapp.data.UIEvent

@Composable
fun SignUpScreen(navController: NavHostController, loginViewModel: LoginViewModel) {

    Surface(color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))
            Spacer(modifier = Modifier.height(20.dp))

            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.first_name),
                painterResource = painterResource(id = R.drawable.profile),
                onTextSelected = {
                    loginViewModel.onEvent(UIEvent.FirstNameChanged(it))

                },
                errorStatus = loginViewModel.registrationUIState.value.firstNameError
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.last_name),
                painterResource = painterResource(id = R.drawable.profile),
                onTextSelected = {
                    loginViewModel.onEvent(UIEvent.LastNameChanged(it))

                },
                errorStatus = loginViewModel.registrationUIState.value.lastNameError
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource = painterResource(id = R.drawable.message),
                onTextSelected = {
                    loginViewModel.onEvent(UIEvent.EmailChanged(it))

                },
                errorStatus = loginViewModel.registrationUIState.value.emailError
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource = painterResource(id = R.drawable.lock),
                onTextSelected = {
                    loginViewModel.onEvent(UIEvent.PasswordChanged(it))

                },
                errorStatus = loginViewModel.registrationUIState.value.passwordError
            )
            Spacer(modifier = Modifier.height(80.dp)
            )
            ButtonComponent(value = stringResource(id = R.string.register),
            onButtonClick = {
                loginViewModel.onEvent(UIEvent.RegisterButtonClicked)
                navController.navigate("first", ) // Navigate to the Groups screen
            })
            Spacer(modifier = Modifier.height(20.dp)
            )
            DividerTextComponent()

            ClickableLoginTextComponent(tryingToLogin = true, onLoginClicked = {
                navController.navigate("login") // Replace "login" with your actual login screen route
            })
        }
    }
}