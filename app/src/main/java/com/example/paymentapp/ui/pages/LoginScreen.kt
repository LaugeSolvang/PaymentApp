package com.example.paymentapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.paymentapp.R
import com.example.paymentapp.components.ButtonComponent
import com.example.paymentapp.components.ClickableLoginTextComponent
import com.example.paymentapp.components.DividerTextComponent
import com.example.paymentapp.components.HeadingTextComponent
import com.example.paymentapp.components.MyTextFieldComponent
import com.example.paymentapp.components.NormalTextComponent
import com.example.paymentapp.components.PasswordTextFieldComponent
import com.example.paymentapp.components.UnderLinedNormalTextComponent
import com.example.paymentapp.data.LoginViewModel
import com.example.paymentapp.data.UIEvent

@Composable
fun LoginScreen (navController: NavHostController, loginViewModel: LoginViewModel = viewModel()) {

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()
        ) {

            NormalTextComponent(value = stringResource(id = R.string.login))
            HeadingTextComponent(value = stringResource(id = R.string.welcome))
            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(40.dp))
            UnderLinedNormalTextComponent(value = stringResource(id = R.string.forgot_password))

            Spacer(modifier = Modifier.height(40.dp)
            )
            ButtonComponent(value = stringResource(id = R.string.login),
                onButtonClick = {
                    loginViewModel.onEvent(UIEvent.LoginButtonClicked)
                    navController.navigate("first") // Navigate to the Groups screen
                })

            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = false, onLoginClicked = {
                navController.navigate("signup") // Replace "login" with your actual login screen route
            })

        }
    }
}
