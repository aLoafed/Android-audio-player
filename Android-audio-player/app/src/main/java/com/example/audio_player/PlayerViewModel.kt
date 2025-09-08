package com.example.audio_player

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.hsv
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toColorLong
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.LcdGrey
import com.example.audio_player.ui.theme.LcdOrange
class PlayerViewModel(
    applicationContext: Context,
) : ViewModel() {
    var isPlaying by mutableStateOf(false)
        private set
    var duration by mutableFloatStateOf(1f) // Length of song
        private set
    var currentSongPosition by mutableFloatStateOf((0f)) // Current position in song
        private set
    var currentAlbumArt by mutableStateOf(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.file_not_found_image).asImageBitmap())
        private set
    var songIterator by mutableIntStateOf(0)
        private set
    var selectedAlbum by mutableStateOf("")
        private set
    var playingFromSongsScreen by mutableStateOf(true)
        private set
    var albumSongInfo = mutableListOf<SongInfo>()
        private set
    //========================= Theme changing =========================
    //============================ Colours ===========================
    var primaryColor = LcdGrey
        private set
    var secondaryColor = LcdBlueWhite
        private set
    var tertiaryColor = LcdOrange
        private set
    var backgroundColor = LcdGrey
        private set
    var textColor = Color.White
        private set
    var iconColor = Color.White
        private set
    var eqLevelColor = Color.White
        private set
    var eqTextColor = Color.White
        private set
    var sliderThumbColor = Color.White
        private set
    var sliderTrackColor = Color.White
        private set
    val colorMap = mutableMapOf(
        "Default" to LcdGrey,
        "Red" to Color.Red,
        "Green" to Color.Green,
        "Blue" to Color.Blue,
        "Light blue" to LcdBlueWhite,
        "Yellow" to Color.Yellow,
        "Orange" to Color(0xffFFA500),
        "Black" to Color.Black,
        "White" to Color.White,
        "Pink" to Color(0xffFFC0CB),
        "Purple" to Color(0xffA020F0)
    )
    val otherColorMap = mutableMapOf(
        "Default" to Color.White,
        "Red" to Color.Red,
        "Green" to Color.Green,
        "Blue" to Color.Blue,
        "Light blue" to LcdBlueWhite,
        "Yellow" to Color.Yellow,
        "Orange" to Color(0xffFFA500),
        "Black" to Color.Black,
        "Pink" to Color(0xffFFC0CB),
        "Purple" to Color(0xffA020F0)
    )
    fun updateCustomColors(color: Color, name: String) {
        var tmpName = name
        if (tmpName == "") {
            tmpName = "Custom"
        }
        var i = 1
        while (tmpName in colorMap) {
            tmpName = "$tmpName($i)"
            i ++
        }
        colorMap[tmpName] = color
        otherColorMap[tmpName] = color
    }
    fun removeCustomColors(key: String) {
        colorMap.remove(key)
        otherColorMap.remove(key)
    }
    fun darkenColor(color: Color, factor: Float): Color {
        val hslArray = FloatArray(3)
        ColorUtils.colorToHSL(color.toColorLong().toInt(), hslArray)
        hslArray[2] = (hslArray[2] * factor).coerceIn(0f,1f)
        return Color(ColorUtils.HSLToColor(hslArray))
    }
    fun updateColor(choice: String, color: Color?) {
        if (color != null) {
            when (choice) {
//                "primary" -> {
//                    primaryColor = color
//                }
//                "secondary" -> {
//                    secondaryColor = color
//                }
//                "tertiary" -> {
//                    tertiaryColor = color
//                }
                "background" -> {
                    backgroundColor = color
                }
                "text" -> {
                    textColor = color
                }
                "icon" -> {
                    iconColor = color
                }
                "eqLevel" -> {
                    eqLevelColor = color
                }
                "eqText" -> {
                    eqTextColor = color
                }
                "sliderThumb" -> {
                    sliderThumbColor = color
                }
                "sliderTrack" -> {
                    sliderTrackColor = color
                }
            }
        }
    }
    fun updateAlbumSongInfo(list: MutableList<SongInfo>) {
        albumSongInfo = list
    }
    fun updatePlayingFromSongsScreen(state: Boolean) {
        playingFromSongsScreen = state
    }
    fun updateSelectedAlbum(album: String) {
        selectedAlbum = album
    }
    fun updateSongIterator(iteration: Int) {
        songIterator = iteration
    }
    fun incrementSongIterator(increment: Int) {
        songIterator += increment
    }
    fun updateAlbumArt(image: ImageBitmap) {
        currentAlbumArt = image
    }
    fun updateCurrentSongPosition(time: Long) {
        currentSongPosition = time.toFloat() / 1000f
    }
    fun updateIsPlaying(state: Boolean) {
        isPlaying = state
    }
    fun updateSongDuration(time: Long) {
        duration = time.toFloat()
    }
    fun updateSongPosition(player: ExoPlayer, time: Long) {
        player.seekTo(time * 1000)
    }
}