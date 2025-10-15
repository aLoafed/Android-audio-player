package com.example.audio_player

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.LcdGrey
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

@Serializable
data class SettingsData(
    val backgroundColor: Int = LcdGrey.toArgb(),
    val textColor: Int = Color.White.toArgb(),
    val iconColor: Int = Color.White.toArgb(),
    val eqLevelColor: Int = Color.White.toArgb(),
    val eqTextColor: Int = LcdBlueWhite.toArgb(),
    val sliderThumbColor: Int = Color.White.toArgb(),
    val sliderTrackColor: Int = changeBrightness(Color.White.toArgb(), 0.8f).toArgb(),
    val customColors: Map<String, Int>,
    val showBasicLoadingScreen: Boolean = true,
    val showEqualiser: Boolean = true,
)

class SettingsManager(context: Context) {
    private val settingsFilePath = File(context.filesDir, "app_settings.json")
    private val json = Json { prettyPrint = true }
    fun saveSettings(settingsData: SettingsData) {
        settingsFilePath.writeText(json.encodeToString(settingsData))
    }

    fun loadSettings(): SettingsData {
        val isFileCreated = settingsFilePath.createNewFile()
        if (isFileCreated) {
            // Fills with default data if json does not exist
            val settingsData = SettingsData(
                customColors = mapOf()
            )
            saveSettings(settingsData)
        }
        return json.decodeFromString<SettingsData>(settingsFilePath.readText())
    }
}
