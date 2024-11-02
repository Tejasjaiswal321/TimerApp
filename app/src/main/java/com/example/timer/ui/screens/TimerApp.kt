package com.example.timer.ui.screens

import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.timer.ui.TimerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TimerApp(timerViewModel: TimerViewModel, onBackPressed: () -> Boolean) {


    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Timer")

    }
}

suspend fun oddEvenTimer(
    oddTime: Int,
    evenTime: Int,
    cycles: Int,
    beepDuration: Int,
    soundUri: Uri,
    contentResolver: ContentResolver
) {
    repeat(cycles) {
        delay(oddTime * 60 * 1000L)
        playBeep(soundUri, beepDuration, contentResolver)
        delay(evenTime * 60 * 1000L)
        playBeep(soundUri, beepDuration, contentResolver)
    }
}

suspend fun randomTimer(
    times: List<Int>,
    beepDuration: Int,
    soundUri: Uri,
    contentResolver: ContentResolver
) {
    times.forEach { time ->
        delay(time * 60 * 1000L)
        playBeep(soundUri, beepDuration, contentResolver)
    }
}

fun playBeep(soundUri: Uri, duration: Int, contentResolver: ContentResolver) {
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(contentResolver.openAssetFileDescriptor(soundUri, "r")!!.fileDescriptor)
        prepare()
        start()
    }

    Timer(duration.toLong() * 1000) {
        mediaPlayer.stop()
        mediaPlayer.release()
    }.start()
}

class Timer(private val delay: Long, private val action: () -> Unit) {
    fun start() {
        Thread.sleep(delay)
        action()
    }
}
