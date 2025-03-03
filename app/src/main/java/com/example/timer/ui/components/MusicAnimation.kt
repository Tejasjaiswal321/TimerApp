package com.example.timer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun MusicAnimation() {
    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.7f))
    ) {
        Text(
            "Time \nis\n up!",
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun MusicAnimationPreview(modifier: Modifier = Modifier) {
    MusicAnimation()
}