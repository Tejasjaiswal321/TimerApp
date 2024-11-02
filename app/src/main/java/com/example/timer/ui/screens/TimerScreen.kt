package com.example.timer.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.timer.ui.TimerViewModel


@Composable
fun TimerScreen(viewModel: TimerViewModel, onBackPressed: () -> Unit) {
    val timerProgress by viewModel.timerProgress.collectAsState()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = viewModel.currentCycle.collectAsState().value.toString())
            Spacer(modifier = Modifier.size(10.dp))
            Canvas(modifier = Modifier.size(200.dp)) {
                val angle = 360 * timerProgress
                drawArc(
                    color = Color.Blue,
                    startAngle = -90f,
                    sweepAngle = angle,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            if (isMusicPlaying) {
                // Music Animation (Simple Pulsing Effect)
                MusicAnimation()
            }
        }
    }
}


