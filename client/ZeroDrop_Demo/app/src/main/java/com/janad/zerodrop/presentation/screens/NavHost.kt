package com.janad.zerodrop.presentation.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.janad.zerodrop.data.UserPreferences
import com.janad.zerodrop.presentation.screens.auth.LoginScreen
import com.janad.zerodrop.presentation.screens.auth.RegisterScreen
import com.janad.zerodrop.presentation.screens.main.TopBar
import com.janad.zerodrop.presentation.viewmodels.ViewModel

// Object to hold screen route constants
object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

// Main navigation host composable function
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),onToggleTheme: ()-> Unit) {
    // ViewModel instance
    val viewModel: ViewModel = hiltViewModel()
    // State to control visibility of query screen
    var showQueryScreen by remember { mutableStateOf(false) }

    // Get current context
    val context = LocalContext.current
    // Collect token flow from UserPreferences
    val tokenFlow = UserPreferences(context).tokenFlow.collectAsState(initial = "")
    // State to control visibility of splash screen
    var showSplash by remember { mutableStateOf(true) }
    // Effect to hide splash screen after a delay
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        showSplash = false
    }
    // Show loading screen if splash is visible
    if (showSplash) {
        LoadingScreen()
    } else {
        // Determine start destination based on token presence
        val startDestination =
            if (tokenFlow.value?.isNotEmpty() ?: false) Screen.HOME else Screen.LOGIN

        // Navigation host
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            // Login screen composable
            composable(Screen.LOGIN) {
                LoginScreen(viewModel = viewModel, navController = navController)
            }
            composable(Screen.REGISTER) {
                RegisterScreen(viewModel = viewModel, navController = navController)
            }
            // Home screen composable with TopBar
            composable(Screen.HOME) {
                TopBar(
                    showQueryScreen = showQueryScreen,
                    onShowQueryScreenChange = { show ->
                        showQueryScreen = show
                    }, onToggleTheme = onToggleTheme
                )
            }
        }
    }
}
