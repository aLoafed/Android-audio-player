package com.example.audio_player

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList

class SettingsData(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    var preferencesList: MutableList<Preferences> = mutableListOf()
    var keys = mutableListOf("")
    suspend fun addCustomColor(keyName: String, color: Color) {
        val customColorKey = longPreferencesKey(keyName)
        keys.add(keyName)
        dataStore.edit { prefs ->
            prefs[customColorKey] = color.toColorLong()
        }
    }
//    suspend fun initKeys() {
//        dataStore.data.toList(preferencesList)
//        for (i in 0 until preferencesList.count()) {
//            preferencesList.
//        }
//    }
    suspend fun readKey(keyName: String): Long? {
        val key = longPreferencesKey(keyName)
        val prefs = dataStore.data.first()
        return prefs[key]
    }
}