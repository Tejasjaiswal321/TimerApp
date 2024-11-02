package com.example.timer.ui

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.timer.data.Mode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

    val _mode = MutableStateFlow(Mode.OddEven)
    val mode get() = _mode
    var oddTime = mutableStateOf("")
    var evenTime = mutableStateOf("")
    var cycleCount = mutableStateOf("")
    var randomTimes = mutableStateOf("")
    var beepDuration = mutableStateOf("5")
    var selectedSoundUri = mutableStateOf("")

    init {
        loadPreferences()
    }
    fun changeMode(mode: Mode){
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
                    selectedSoundUri.value = preferences[PreferencesKeys.SELECTED_SOUND_URI] ?: ""
                }
                .collect{} // Collect the flow to apply the changes
        }
    }

    fun savePreferences() {
        val context = getApplication<Application>().applicationContext
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[PreferencesKeys.ODD_TIME] = oddTime.value
                preferences[PreferencesKeys.EVEN_TIME] = evenTime.value
                preferences[PreferencesKeys.CYCLE_COUNT] = cycleCount.value
                preferences[PreferencesKeys.RANDOM_TIMES] = randomTimes.value
                preferences[PreferencesKeys.BEEP_DURATION] = beepDuration.value
                preferences[PreferencesKeys.SELECTED_SOUND_URI] = selectedSoundUri.value
            }
        }
    }

    object PreferencesKeys {
        val ODD_TIME = stringPreferencesKey("odd_time")
        val EVEN_TIME = stringPreferencesKey("even_time")
        val CYCLE_COUNT = stringPreferencesKey("cycle_count")
        val RANDOM_TIMES = stringPreferencesKey("random_times")
        val BEEP_DURATION = stringPreferencesKey("beep_duration")
        val SELECTED_SOUND_URI = stringPreferencesKey("selected_sound_uri")
    }

    fun handleSoundPickerResult(uri: Uri, contentResolver: ContentResolver) {
        val soundUri = uri.toString()
        selectedSoundUri.value = soundUri
        savePreferences()
    }

    fun getSoundFileName(uri: Uri, contentResolver: ContentResolver): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }
}
