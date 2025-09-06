package com.janad.zerodrop.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.janad.zerodrop.presentation.screens.Screen
import com.janad.zerodrop.presentation.viewmodels.ViewModel

@Composable
fun RegisterScreen(viewModel: ViewModel = hiltViewModel(),navController: NavController) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val registerResult by viewModel.registerResult.collectAsState()
    val authError by viewModel.authError.collectAsState()

    // Trigger register when user presses the button
    fun doRegister() {
        viewModel.register(username, password)
    }
    //Navigate to home screen if register is successful
    LaunchedEffect(registerResult) {
        if(registerResult?.token?.isNotEmpty() ?:false ){
            navController.navigate(Screen.HOME){    popUpTo(Screen.LOGIN) { inclusive = true }}
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFff7e5f), Color(0xFFfeb47b))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = "Create Account",
                fontSize = 30.sp,
                color = Color(0xFFff7e5f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Username input
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username",color = Color.Black) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password",color = Color.Black) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    Icon(
                        imageVector = image,
                        contentDescription = null,
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Error message
            if (authError.isNotEmpty()) {
                Text(
                    text = authError,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Register button
            Button(
                onClick = { doRegister() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff7e5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Register", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Already have an account? Login",
                color = Color.Blue,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.LOGIN)
                }
            )
        }
    }
}
