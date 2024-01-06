package com.example.paymentapp.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paymentapp.R
import com.example.paymentapp.ui.theme.componentShapes
import java.security.KeyStore.TrustedCertificateEntry

@Composable
fun NormalTextComponent(value:String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
    , color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )

}

@Composable
fun HeadingTextComponent(value:String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        )
        , color = colorResource(id = R.color.colorText),
        textAlign = TextAlign.Center
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextFieldComponent(labelValue:String, painterResource: Painter,
                         onTextSelected: (String) -> Unit
) {

    val textValue = remember {
        mutableStateOf("")
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorResource(id = R.color.colorPrimary),
            focusedLabelColor = colorResource(id = R.color.colorPrimary),
            cursorColor = colorResource(id = R.color.colorPrimary),
            containerColor = colorResource(id = R.color.colorBG)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
        maxLines = 1,
        value = textValue.value,
        onValueChange = {
            textValue.value = it
            onTextSelected(it)
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextFieldComponent(labelValue:String, painterResource: Painter,
                               onTextSelected: (String) -> Unit
) {

    val localFocusManager = LocalFocusManager.current
    val password = remember {
        mutableStateOf("")
    }

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorResource(id = R.color.colorPrimary),
            focusedLabelColor = colorResource(id = R.color.colorPrimary),
            cursorColor = colorResource(id = R.color.colorPrimary),
            containerColor = colorResource(id = R.color.colorBG)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        singleLine = true,
        maxLines = 1,
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        value = password.value,
        onValueChange = {
            password.value = it
            onTextSelected(it)
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        },
        trailingIcon = {

            val iconImage = if (passwordVisible.value) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            var description = if (passwordVisible.value) {
                stringResource(id = R.string.hide_password)
            } else {
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = iconImage, contentDescription = description)
            }

        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun ButtonComponent(value: String, onClick: () -> Unit){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {

        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        colorResource(id = R.color.colorSecondary),
                        colorResource(id = R.color.colorPrimary)
                    )
                ),
                shape = RoundedCornerShape(50.dp)
            ),
            contentAlignment = Alignment.Center
        ){
            Text(text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }

    }
}

@Composable
fun DividerTextComponent(){
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = colorResource(id = R.color.colorGrey),
            thickness = 1.dp
        )

        Text(modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.or),
            fontSize = 18.sp,
            color = colorResource(id = R.color.colorText))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = colorResource(id = R.color.colorGrey),
            thickness = 1.dp)

    }
}

@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean = true, onLoginClicked: () -> Unit) {
    val initialText = if (tryingToLogin) "Already have an account? " else "Don't have an account yet? "
    val clickablePart = if (tryingToLogin) "Login" else "Register"

    val annotatedString = buildAnnotatedString {
        append(initialText)
        pushStringAnnotation(tag = "login", annotation = clickablePart)
        withStyle(style = SpanStyle(color = colorResource(id = R.color.colorPrimary))) {
            append(clickablePart)
        }
        pop() // Don't forget to pop the annotation
    }

    ClickableText(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 21.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal,
            textAlign = TextAlign.Center
        ),
        text = annotatedString, onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "login", start = offset, end = offset)
                .firstOrNull()?.also {
                    onLoginClicked()
                }
        }
    )
}


@Composable
fun UnderLinedNormalTextComponent(value:String){
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        )
        , color = colorResource(id = R.color.colorGrey),
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
    )

}