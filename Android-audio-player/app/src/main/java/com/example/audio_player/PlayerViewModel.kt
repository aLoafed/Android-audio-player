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
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
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
    var currentTitle = ""
    var primaryColor = LcdGrey
        private set
    var secondaryColor = LcdBlueWhite
        private set
    var tertiaryColor = LcdOrange
        private set
    var backgroundColor = LcdGrey
        private set
    fun updateColor(choice: String, color: String) {
        when (choice) {
            "primary" -> {
                primaryColor = when (color) {
                    "Default" -> LcdGrey
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
                    else -> LcdGrey
                }
            }
            "secondary" -> {
                secondaryColor = when (color) {
                    "Default" -> LcdGrey
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
                    "Default" -> LcdGrey
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
                    else -> LcdOrange
                }
            }
            "background" -> {
                backgroundColor = when (color) {
                    "Default" -> LcdGrey
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
                    else -> LcdGrey
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