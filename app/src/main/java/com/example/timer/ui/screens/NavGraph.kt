package com.example.timer.ui.screens

import android.content.Intent
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
    soundPickerLauncher: ActivityResultLauncher<Intent>,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Settings.name,
        modifier = modifier
            .padding(paddingValues)
            .padding(30.dp)
            .fillMaxSize()
    ) {
        composable(Screen.Settings.name) {
            Settings(
                timerViewModel = timerViewModel,
                onPickSound = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "audio/*" // MIME type for audio files
                        flags =
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    }
                    soundPickerLauncher.launch(intent)
//                    soundPickerLauncher.launch("audio/*") //todo check
                },
                onNextClick = {
                    navController.navigate(Screen.Timer.name)
                }
            )
        }
        composable(Screen.Timer.name) {
            TimerScreen(
                viewModel = timerViewModel,
                onBackPressed = { navController.clearBackStack(Screen.Settings.name) }
            )
        }
    }
}