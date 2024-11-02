package com.example.timer.util

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val ODD_TIME = stringPreferencesKey("odd_time")
    val EVEN_TIME = stringPreferencesKey("even_time")
    val CYCLE_COUNT = stringPreferencesKey("cycle_count")
    val RANDOM_TIMES = stringPreferencesKey("random_times")
    val BEEP_DURATION = stringPreferencesKey("beep_duration")
    val SELECTED_SOUND_URI = stringPreferencesKey("selected_sound_uri")
}