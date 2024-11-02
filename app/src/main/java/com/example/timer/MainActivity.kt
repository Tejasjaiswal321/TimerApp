package com.example.timer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.timer.ui.vm.TimerViewModel
import com.example.timer.ui.vm.TimerViewModelFactory
import com.example.timer.ui.screens.NavGraph
import com.example.timer.ui.theme.TimerTheme

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_PICK_SOUND = 1001
    val tag = "MainActivity"

    private val timerViewModel: TimerViewModel by lazy {
        ViewModelProvider(this, TimerViewModelFactory(application)).get(TimerViewModel::class.java)
    }

    private val soundPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                try {
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    timerViewModel.handleSoundPickerResult(it, contentResolver)
                } catch (e: SecurityException) {
                    Log.e("TimerApp", "Persistable permission not available: ${e.message}")
                    // Handle the exception as needed
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(timerViewModel, soundPickerLauncher, innerPadding)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(tag,"onActivityResult : requestCode: $requestCode, resultCode: $resultCode, data.data: ${data?.data}")
        if (requestCode == REQUEST_CODE_PICK_SOUND && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Persist access permission
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timerViewModel.stopMediaPlayer()
        timerViewModel.stopTimerJob("Back pressed")
    }
}


