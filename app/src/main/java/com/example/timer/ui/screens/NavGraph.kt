package com.example.timer.ui.screens

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timer.data.Screen
import com.example.timer.ui.TimerViewModel


@Composable
fun NavGraph(
    timerViewModel: TimerViewModel,
    soundPickerLauncher: ActivityResultLauncher<String>,
    paddingValues:PaddingValues,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Settings.name,
        modifier = modifier.padding(paddingValues).padding(30.dp).fillMaxSize()
    ) {
        composable(Screen.Settings.name) {
            Settings(
                timerViewModel = timerViewModel,
                onPickSound = { soundPickerLauncher.launch("audio/*") },
                onNextClick = { navController.navigate(Screen.Timer.name) }
            )
        }
        composable(Screen.Timer.name) {
            TimerApp(
                timerViewModel = timerViewModel,
                onBackPressed = {navController.clearBackStack(Screen.Settings.name)}
            )
        }

    }

}