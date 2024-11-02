package com.example.timer.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timer.ui.TimerViewModel
import com.example.timer.ui.components.CircularProgressArc
import com.example.timer.ui.components.MusicAnimation
import com.example.timer.util.minutesToMillis


@Composable
fun TimerScreen(viewModel: TimerViewModel, onBackPressed: () -> Unit) {
    val tag = "TimerScreen"
    val evenIntervalMinutes = viewModel.evenIntervalMinutes.value.toLongOrNull() ?: 1L
    val oddIntervalMinutes = viewModel.oddIntervalMinutes.value.toLongOrNull() ?: 1L
    val evenTimeLeft by viewModel.evenTimeLeft.collectAsState()
    val oddTimeLeft by viewModel.oddTimeLeft.collectAsState()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsState()
    val currentCycle = viewModel.currentCycle.collectAsState().value.toString()
    val totalCycleCount = viewModel.cycleCount.value
    LaunchedEffect(key1 = evenTimeLeft) {
        Log.d(tag, "evenTimeLeft = $evenTimeLeft")
    }
    LaunchedEffect(key1 = oddTimeLeft) {
        Log.d(tag, "oddTimeLeft = $oddTimeLeft")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Cycle : $currentCycle of $totalCycleCount", fontSize = 20.sp)
            Spacer(modifier = Modifier.size(20.dp))
            CircularProgressArc(oddTimeLeft, oddIntervalMinutes.minutesToMillis())
            Spacer(modifier = Modifier.size(20.dp))
            CircularProgressArc(evenTimeLeft, evenIntervalMinutes.minutesToMillis(), Color.Yellow)
        }
        if (isMusicPlaying) {
            MusicAnimation()
        }
    }
}


@Preview
@Composable
fun TimerScreenPreview(modifier: Modifier = Modifier) {
    TimerScreen(
        viewModel = MockTimerViewModel()
    ) {
    }
}

class MockTimerViewModel : TimerViewModel(Application()) {
}