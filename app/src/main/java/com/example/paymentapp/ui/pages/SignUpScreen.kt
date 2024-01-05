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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.paymentapp.R
import com.example.paymentapp.components.ButtonComponent
import com.example.paymentapp.components.ClickableLoginTextComponent
import com.example.paymentapp.components.DividerTextComponent
import com.example.paymentapp.components.HeadingTextComponent
import com.example.paymentapp.components.MyTextFieldComponent
import com.example.paymentapp.components.NormalTextComponent
import com.example.paymentapp.components.PasswordTextFieldComponent

@Composable
fun SignUpScreen() {

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
                painterResource = painterResource(id = R.drawable.profile)
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.last_name),
                painterResource = painterResource(id = R.drawable.profile)
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource = painterResource(id = R.drawable.message)
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource = painterResource(id = R.drawable.lock)
            )
            Spacer(modifier = Modifier.height(80.dp)
            )
            ButtonComponent(value = stringResource(id = R.string.register)
            )
            Spacer(modifier = Modifier.height(20.dp)
            )
            DividerTextComponent()

            ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {

            })

        }
    }
}

@Preview
@Composable
fun DefaultPreviewOfSignUpScreen(){
    SignUpScreen()
}