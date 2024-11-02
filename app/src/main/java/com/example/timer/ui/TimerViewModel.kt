package com.example.timer.ui

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timer.data.Mode
import com.example.timer.util.PreferencesKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.stream.IntStream.range

private val Context.dataStore by preferencesDataStore(name = "settings")


class TimerViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "TimerViewModel"

    private val _mode = MutableStateFlow(Mode.OddEven)
    val mode get() = _mode
    var oddTime = mutableStateOf("")
    var evenTime = mutableStateOf("")
    var cycleCount = mutableStateOf("")
    var randomTimes = mutableStateOf("")
    var beepDuration = mutableStateOf("5")
    private val _selectedSoundUri = MutableStateFlow(Uri.parse(""))
    val selectedSoundUri: StateFlow<Uri> get() = _selectedSoundUri

    var mediaPlayer: MediaPlayer? = null


    private val _timerProgress = MutableStateFlow(1f) // 1.0 for full progress, 0.0 when timer ends
    val timerProgress: StateFlow<Float> get() = _timerProgress

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> get() = _isMusicPlaying

    private val _currentCycle = MutableStateFlow(0)
    val currentCycle: StateFlow<Int> get() = _currentCycle


    suspend fun startTimer(duration: Long) {
        var remainingTime = duration
        _isMusicPlaying.value = false
        _timerProgress.value = 1f
        while (remainingTime > 0) {
            _timerProgress.value = remainingTime / duration.toFloat()
            delay(1000) // Update every second
            remainingTime -= 1000
        }
        _timerProgress.value = 0f
        _isMusicPlaying.value = true // Start music animation when timer ends
    }

    init {
        loadPreferences()
    }

    fun changeMode(mode: Mode) {
        _mode.value = mode
    }


    private fun loadPreferences() {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences ->
                    oddTime.value = preferences[PreferencesKeys.ODD_TIME] ?: ""
                    evenTime.value = preferences[PreferencesKeys.EVEN_TIME] ?: ""
                    cycleCount.value = preferences[PreferencesKeys.CYCLE_COUNT] ?: ""
                    randomTimes.value = preferences[PreferencesKeys.RANDOM_TIMES] ?: ""
                    beepDuration.value = preferences[PreferencesKeys.BEEP_DURATION] ?: "5"
                    _selectedSoundUri.value =
                        Uri.parse(preferences[PreferencesKeys.SELECTED_SOUND_URI] ?: "")
                }
                .collect {} // Collect the flow to apply the changes
        }
    }

    fun savePreferences() {
        val context = getApplication<Application>().applicationContext
        Log.d(tag, "saving Preferences")
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.ODD_TIME] = oddTime.value
                preferences[PreferencesKeys.EVEN_TIME] = evenTime.value
                preferences[PreferencesKeys.CYCLE_COUNT] = cycleCount.value
                preferences[PreferencesKeys.RANDOM_TIMES] = randomTimes.value
                preferences[PreferencesKeys.BEEP_DURATION] = beepDuration.value
                preferences[PreferencesKeys.SELECTED_SOUND_URI] =
                    selectedSoundUri.value.path.toString()
            }
        }
    }

    fun handleSoundPickerResult(uri: Uri, contentResolver: ContentResolver) {
        _selectedSoundUri.value = uri
        savePreferences()
    }

    fun getSoundFileName(uri: Uri, contentResolver: ContentResolver): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    fun oddEvenTimer(
        oddTime: Int,
        evenTime: Int,
        cycles: Int,
        beepDuration: Long,
        soundUri: Uri = selectedSoundUri.value,
        contentResolver: ContentResolver
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            for (i in range(1, cycles + 1)) {
                _currentCycle.value = i
                startTimer(oddTime * 1000L)
                playBeep(soundUri, beepDuration, contentResolver)
                startTimer(evenTime * 1000L)
                playBeep(soundUri, beepDuration, contentResolver)
            }
        }
    }

    suspend fun randomTimer(
        times: List<Int>,
        beepDuration: Long,
        soundUri: Uri,
        contentResolver: ContentResolver
    ) {
        times.forEach { time ->
            delay(time * 60 * 1000L)
            playBeep(soundUri, beepDuration, contentResolver)
        }
    }

    suspend fun playBeep(
        soundUri: Uri = selectedSoundUri.value,
        duration: Long,
        contentResolver: ContentResolver
    ) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(contentResolver.openAssetFileDescriptor(soundUri, "r")!!.fileDescriptor)
            prepare()
            start()
            Log.d(tag, "playbeep with uri = $soundUri")
        }
        delay(duration * 1000L)

        stopMediaPlayer()
    }

    fun stopMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}
