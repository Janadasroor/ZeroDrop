package com.janad.zerodrop.presentation.screens.main

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.janad.zerodrop.data.UserPreferences
import com.janad.zerodrop.presentation.screens.LoadingScreen
import com.janad.zerodrop.presentation.screens.auth.LoginScreen
import com.janad.zerodrop.presentation.screens.auth.RegisterScreen
import com.janad.zerodrop.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.delay

object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),onToggleTheme: ()-> Unit) {
    val mainViewModel: MainViewModel = hiltViewModel()
    var showQueryScreen by rememberSaveable { mutableStateOf(false) }
     val context = LocalContext.current
    val tokenFlow = UserPreferences(context).tokenFlow.collectAsState(initial = "")
    var showSplash by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }
    if (showSplash) {
        LoadingScreen()
    } else {
        val startDestination =
            if (tokenFlow.value?.isNotEmpty() ?: false) Screen.MAIN else Screen.LOGIN

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.LOGIN) {
                LoginScreen(mainViewModel = mainViewModel, navController = navController)
            }
            composable(Screen.REGISTER) {
                RegisterScreen(mainViewModel = mainViewModel, navController = navController)
            }
            composable(Screen.MAIN) {
                //     ProductsScreen(viewModel = viewModel)
                TopBar(
                    showQueryScreen = showQueryScreen,
                    onShowQueryScreenChange = { show ->
                        showQueryScreen = show
                    }, onToggleTheme = onToggleTheme
                )
            }
        }
    }}
