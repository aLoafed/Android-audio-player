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
    fun darkenColor(color: Color, factor: Float): Color {
        val hslArray = FloatArray(3)
        ColorUtils.colorToHSL(color.toColorLong().toInt(), hslArray)
        hslArray[2] = (hslArray[2] * factor).coerceIn(0f,1f)
        return Color(ColorUtils.HSLToColor(hslArray))
    }
    fun updateColor(choice: String, color: String) {
        when (choice) {
            "primary" -> {
                primaryColor = when (color) {
                    "Default" -> LcdGrey
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "White" -> Color.White
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> LcdGrey
                }
            }
            "secondary" -> {
                secondaryColor = when (color) {
                    "Default" -> LcdBlueWhite
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "White" -> Color.White
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> LcdBlueWhite
                }
            }
            "tertiary" -> {
                tertiaryColor = when (color) {
                    "Default" -> LcdOrange
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "White" -> Color.White
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> LcdOrange
                }
            }
            "background" -> {
                backgroundColor = when (color) {
                    "Default" -> LcdGrey
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "White" -> Color.White
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> LcdGrey
                }
            }
            "text" -> {
                textColor = when (color) {
                    "Default" -> Color.White
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> Color.White
                }
            }
            "icon" -> {
                iconColor = when (color) {
                    "Default" -> Color.White
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> Color.White
                }
            }
            "eqLevel" -> {
                eqLevelColor = when (color) {
                    "Default" -> Color.White
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> Color.White
                }
            }
            "eqText" -> {
                eqTextColor = when (color) {
                    "Default" -> LcdBlueWhite
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "White" -> Color.White
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> LcdBlueWhite
                }
            }
            "sliderThumb" -> {
                sliderThumbColor = when (color) {
                    "Default" -> Color.White
                    "Red" -> Color.Red
                    "Green" -> Color.Green
                    "Blue" -> Color.Blue
                    "Light blue" -> LcdBlueWhite
                    "Yellow" -> Color.Yellow
                    "Orange" -> Color(0xffFFA500)
                    "Black" -> Color.Black
                    "Grey" -> Color.Gray
                    "White" -> Color.White
                    "Pink" -> Color(0xffFFC0CB)
                    "Purple" -> Color(0xffA020F0)
                    "Cyan" -> Color.Cyan
                    "Magenta" -> Color.Magenta
                    else -> Color.White
                }
            }
            "sliderTrack" -> {
                sliderTrackColor = when (color) {
                    "Default" -> Color(0.74f,0.74f,0.74f)
                    "Red" -> darkenColor(Color.Red, 0.74f)
                    "Green" -> darkenColor(Color.Green, 0.74f)
                    "Blue" -> darkenColor(Color.Blue, 0.74f)
                    "Light blue" -> darkenColor(LcdBlueWhite,0.74f)
                    "Yellow" -> darkenColor(Color.Yellow,0.74f)
                    "Orange" -> darkenColor(Color(0xffFFA500),0.74f)
                    "Black" -> darkenColor(Color.Black,0.74f)
                    "Grey" -> darkenColor(Color.Gray,0.74f)
                    "White" -> darkenColor(Color.White,0.74f)
                    "Pink" -> darkenColor(Color(0xffFFC0CB),0.74f)
                    "Purple" -> darkenColor(Color(0xffA020F0),0.74f)
                    "Cyan" -> darkenColor(Color.Cyan,0.74f)
                    "Magenta" -> darkenColor(Color.Magenta,0.74f)
                    else -> Color(0.74f,0.74f,0.74f)
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