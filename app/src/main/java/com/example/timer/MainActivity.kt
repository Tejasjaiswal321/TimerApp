package com.example.timer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.timer.ui.screens.NavGraph
import com.example.timer.ui.TimerViewModel
import com.example.timer.ui.TimerViewModelFactory
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    //    val application = getApplication()
    private val timerViewModel: TimerViewModel by lazy {
        ViewModelProvider(this, TimerViewModelFactory(application)).get(TimerViewModel::class.java)
    }
    private val soundPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            playSelectedSound(uri)
            timerViewModel.handleSoundPickerResult(it, contentResolver)
        }
    }
    private fun playSelectedSound(uri: Uri) {
        println("uri = $uri");
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(this@MainActivity, uri)
            prepare()
            start()
        }

        // Stop playback after 5 seconds
        val job = this.lifecycleScope.launch {
            delay(5000)
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        // If the activity is destroyed before the delay, cancel the job and release the MediaPlayer
        this.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                job.cancel()
                mediaPlayer.release()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appContext = LocalContext.current.applicationContext
            TimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(timerViewModel,soundPickerLauncher,innerPadding)
                }
            }
        }
    }
}


