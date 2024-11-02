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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timer.data.Mode
import com.example.timer.util.PreferencesKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

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

open class TimerViewModel(val context:Context) :ViewModel() {

    private val tag = "TimerViewModel"

    private val _mode = MutableStateFlow(Mode.OddEven)
    val mode get() = _mode
    var oddIntervalMinutes = mutableStateOf("")
    var evenIntervalMinutes = mutableStateOf("")
    var cycleCount = mutableStateOf("")
    var randomTimes = mutableStateOf("")
    var beepDuration = mutableStateOf("5")
    private val _selectedSoundUri = MutableStateFlow(Uri.parse(""))
    val selectedSoundUri: StateFlow<Uri> get() = _selectedSoundUri

    var mediaPlayer: MediaPlayer? = null
    var timerJob : Job? = null


    private val _oddTimeLeft = MutableStateFlow(-1L) // 1.0 for full progress, 0.0 when timer ends
    val oddTimeLeft: StateFlow<Long> get() = _oddTimeLeft
    private val _evenTimeLeft = MutableStateFlow(-1L) // 1.0 for full progress, 0.0 when timer ends
    val evenTimeLeft: StateFlow<Long> get() = _evenTimeLeft

    private val _isMusicPlaying = MutableStateFlow(false)
    val isMusicPlaying: StateFlow<Boolean> get() = _isMusicPlaying

    private val _currentCycle = MutableStateFlow(0L)
    val currentCycle: StateFlow<Long> get() = _currentCycle


    suspend fun startTimer(timeLeft: MutableStateFlow<Long>, duration: Long) {
        while (timeLeft.value > 0) {
            delay(500)
            timeLeft.value -= 500
        }
    }

    init {
        loadPreferences()
    }

    fun changeMode(mode: Mode) {
        _mode.value = mode
    }


    private fun loadPreferences() {
        viewModelScope.launch {
            context.dataStore.data
                .map { preferences ->
                    oddIntervalMinutes.value = preferences[PreferencesKeys.ODD_TIME] ?: ""
                    evenIntervalMinutes.value = preferences[PreferencesKeys.EVEN_TIME] ?: ""
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
        Log.d(tag, "saving Preferences")
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.ODD_TIME] = oddIntervalMinutes.value
                preferences[PreferencesKeys.EVEN_TIME] = evenIntervalMinutes.value
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
        oddIntervalInMin: Long,
        evenIntervalInMin: Long,
        cycles: Long,
        beepDuration: Long,
        soundUri: Uri = selectedSoundUri.value,
        contentResolver: ContentResolver
    ) {
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            for (i in 1..cycles) {
                val totalOddTime = oddIntervalInMin * 1000L
                val totalEvenTime = evenIntervalInMin * 1000L
                _currentCycle.value = i
                _oddTimeLeft.value = totalOddTime
                _evenTimeLeft.value = totalEvenTime
                startTimer(_oddTimeLeft, totalOddTime)
                playBeep(soundUri, beepDuration, contentResolver)
                startTimer(_evenTimeLeft, totalEvenTime)
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
        Log.d(tag,"playBeep start")
        _isMusicPlaying.value = true
        mediaPlayer = MediaPlayer().apply {
            setDataSource(contentResolver.openAssetFileDescriptor(soundUri, "r")!!.fileDescriptor)
            prepare()
            start()
            Log.d(tag, "playbeep with uri = $soundUri")
        }
        delay(duration * 1000L)
        stopMediaPlayer()
        Log.d(tag,"playBeep end")
    }

    fun stopMediaPlayer() {
        Log.d(tag,"stopMediaPlayer called")
        _isMusicPlaying.value = false
        if(mediaPlayer?.isPlaying==true){
            mediaPlayer?.reset()
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }
    fun stopTimerJob(message:String){
        timerJob?.cancel(CancellationException(message))
    }
}
