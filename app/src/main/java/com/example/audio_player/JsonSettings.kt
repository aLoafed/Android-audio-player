package com.example.audio_player

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.LcdGrey
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

@Serializable
data class SettingsData( // May want to add default values in the future
    val backgroundColor: Int,
    val textColor: Int,
    val iconColor: Int,
    val eqLevelColor: Int,
    val eqTextColor: Int,
    val sliderThumbColor: Int,
    val sliderTrackColor: Int,
    val customColors: Map<String, Int>,
    val showBasicLoadingScreen: Boolean,
)

class SettingsManager(context: Context) {
    private val settingsFilePath = File(context.filesDir, "app_settings.json")
    private val json = Json { prettyPrint = true }
    fun saveSettings(settingsData: SettingsData) {
        val jsonColorData = json.encodeToString(settingsData)
        // val writer = settingsFilePath.writer()
        settingsFilePath.writeText(jsonColorData)
        // writer.write(jsonColorData)
    }

    fun loadSettings(): SettingsData {
        val isFileCreated = settingsFilePath.createNewFile()
        if (isFileCreated) {
            val settingsData = SettingsData(
                LcdGrey.toArgb(),
                Color.White.toArgb(),
                Color.White.toArgb(),
                Color.White.toArgb(),
                LcdBlueWhite.toArgb(),
                Color.White.toArgb(),
                Color.White.toArgb(),
                mapOf(),
                true
            )
            saveSettings(settingsData)
        }

        return json.decodeFromString<SettingsData>(settingsFilePath.readText())
    }
}
