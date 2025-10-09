package com.example.audio_player

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.LcdGrey

class PlayerViewModel(
    applicationContext: Context,
) : ViewModel() {
    //========================= Media info =========================
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
    // For SongOptions
    var selectedSong by mutableStateOf(SongInfo(
        "",
        "",
        Uri.EMPTY,
        0f,
        "",
        "",
        ImageBitmap(1,1)
    ))
        private set
    var albumSongInfo = mutableListOf<SongInfo>()
        private set
    var queuedSongs = listOf<SongInfo>()
        private set

    var lastPlayedUnshuffledSong = 0
        private set
    //========================= Playing modes =========================
    var isPlaying by mutableStateOf(false)
        private set
    var playingFromSongsScreen by mutableStateOf(true)
        private set
    var shuffleMode by mutableStateOf(false)
        private set
    var repeatMode by mutableStateOf("normal")
        private set
    //============================ Colours ===========================
    var backgroundColor = LcdGrey
        private set
    var textColor = Color.White
        private set
    var iconColor = Color.White
        private set
    var eqLevelColor = Color.White
        private set
    var eqTextColor = LcdBlueWhite
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
        "Purple" to Color(0xffA020F0),
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
    val customColorMap = mutableMapOf<String, Int>()
    //========================= Miscellaneous =========================
    var loadingFinished by mutableStateOf(false)
        private set

    var showBasicLoadingScreen = true
        private set
    //========================= Updaters =========================
    fun initViewModel(context: Context) {
        val settingsManager = SettingsManager(context)
        val settings = settingsManager.loadSettings()
        Log.d("Neoplayer","$settings")
        backgroundColor = Color(settings.backgroundColor)
        textColor = Color(settings.textColor)
        iconColor = Color(settings.iconColor)
        eqTextColor = Color(settings.eqTextColor)
        eqLevelColor = Color(settings.eqLevelColor)
        sliderThumbColor = Color(settings.sliderThumbColor)
        sliderTrackColor = Color(settings.sliderTrackColor)
        customColorMap.putAll(settings.customColors)
        val intToColorMap = mutableMapOf<String, Color>()
        for (i in settings.customColors.keys) {
            intToColorMap[i] = Color(settings.customColors[i]!!)
        }
        colorMap.putAll(intToColorMap)
        showBasicLoadingScreen = settings.showBasicLoadingScreen
    }
    //========================= Init from Json =========================
    fun updateLoadingScreenChoice(state: Boolean) {
        showBasicLoadingScreen = state
    }
    fun updateFinishedLoading(state: Boolean) {
        loadingFinished = state
    }
    fun updateSelectedOptionsSong(songInfo: SongInfo) {
        selectedSong = songInfo
    }
    fun updateLastPlayedUnshuffledSong() {
        lastPlayedUnshuffledSong = songIterator
    }
    fun updateShuffleMode(boolean: Boolean) {
        shuffleMode = boolean
    }
    fun updateRepeatMode(mode: String) {
        repeatMode = mode
    }
    fun updateCustomColors(color: Color, name: String) {
        colorMap[name] = color
        otherColorMap[name] = color
        customColorMap[name] = color.toArgb()
    }
    fun removeCustomColors(key: String) {
        colorMap.remove(key)
        otherColorMap.remove(key)
    }

    fun updateColor(choice: String, color: Color?) {
        Log.d("Neoplayer", "Choice: $choice, Color: $color")
        if (color != null) {
            when (choice) {
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
    fun updateQueuedSongs(list: List<SongInfo>) {
        queuedSongs = list
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
    fun updateSongPosition(mediaController: MediaController?, time: Long) {
        mediaController?.seekTo(time * 1000)
    }
}