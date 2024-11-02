package com.example.timer.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timer.data.Mode
import com.example.timer.ui.InputRow
import com.example.timer.ui.ModeSelectionDropDown
import com.example.timer.ui.TimerViewModel
import kotlinx.coroutines.launch

@Composable
fun Settings(
    timerViewModel: TimerViewModel,
    onNextClick: () -> Unit,
    onPickSound: () -> Unit
) {
    val mode by timerViewModel.mode.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Timer Settings", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        ModeSelectionDropDown(mode, timerViewModel)
        Spacer(modifier = Modifier.height(16.dp))

        if (mode == Mode.OddEven) {
            // Odd-Even Timer inputs
            InputRow(
                "Odd Duration \n(minutes)",
                timerViewModel.oddTime.value
            ) {
                timerViewModel.oddTime.value = it
                timerViewModel.savePreferences()
            }
            Spacer(modifier = Modifier.height(8.dp))
            InputRow(
                "Even Duration \n(minutes)",
                timerViewModel.evenTime.value
            ) { it: String ->
                timerViewModel.evenTime.value = it
                timerViewModel.savePreferences()
            }

            Spacer(modifier = Modifier.height(8.dp))
            InputRow(
                "Cycle Count",
                timerViewModel.cycleCount.value
            ) { it: String ->
                timerViewModel.cycleCount.value = it
                timerViewModel.savePreferences()
            }
        } else {
            // Random Timer inputs
            InputRow(
                "Enter Random Times \n(comma separated):",
                timerViewModel.randomTimes.value
            ) { it: String ->
                timerViewModel.randomTimes.value = it
                timerViewModel.savePreferences()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Beep duration input
        InputRow(
            "Beep Duration \n(seconds)",
            timerViewModel.beepDuration.value
        ) { it: String ->
            timerViewModel.beepDuration.value = it
            timerViewModel.savePreferences()
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Sound Picker
        Button(onClick = { onPickSound();Log.d("a", "pick sound") }) {
            Text("Select Sound")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        val soundUri = Uri.parse(timerViewModel.selectedSoundUri.value)

        Text(
            "Selected Sound: ${
                timerViewModel.getSoundFileName(soundUri, context.contentResolver)
            }"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onNextClick()
            if (mode == Mode.OddEven) {
                coroutineScope.launch {
                    oddEvenTimer(
                        oddTime = timerViewModel.oddTime.value.toIntOrNull() ?: 0,
                        evenTime = timerViewModel.evenTime.value.toIntOrNull() ?: 0,
                        cycles = timerViewModel.cycleCount.value.toIntOrNull() ?: 0,
                        beepDuration = timerViewModel.beepDuration.value.toIntOrNull() ?: 5,
                        soundUri = Uri.parse(timerViewModel.selectedSoundUri.value),
                        contentResolver = context.contentResolver
                    )
                }
            } else {
                coroutineScope.launch {
                    val times = timerViewModel.randomTimes.value.split(",")
                        .mapNotNull { it.trim().toIntOrNull() }
                    randomTimer(
                        times = times,
                        beepDuration = timerViewModel.beepDuration.value.toIntOrNull() ?: 5,
                        soundUri = Uri.parse(timerViewModel.selectedSoundUri.value),
                        contentResolver = context.contentResolver
                    )
                }
            }
        }) {
            Text("Start Timer")
        }
    }
}

