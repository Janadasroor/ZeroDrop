package com.janad.zerodrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.janad.zerodrop.presentation.screens.main.AppNavHost
import com.janad.zerodrop.ui.theme.ZerodropTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkTheme by remember { mutableStateOf(true) }
            ZerodropTheme(darkTheme){

                AppNavHost(onToggleTheme = {
                    darkTheme =!darkTheme
                })
            }
        }
    }
}

