package com.example.timer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timer.util.Utils

@Composable
fun CircularProgressArc(timeLeft: Long, totalTime: Long, arcColor: Color = Color.Cyan) {
    Box {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawArc(
                color = Color.Gray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Canvas(modifier = Modifier.size(200.dp)) {
            val angle = 360 * timeLeft.toFloat() / totalTime.toFloat()
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            Utils.formatMillisecondsToMMSS(timeLeft),
            Modifier.align(Alignment.Center),
            fontSize = 30.sp
        )
    }
}

@Preview
@Composable
fun CircularProgressArcPreview(modifier: Modifier = Modifier) {
    CircularProgressArc(timeLeft = 100000, totalTime = 300000)
}