package com.example.audio_player

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer

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
    var songInfoIterator by mutableIntStateOf(0)
        private set
    var selectedAlbum by mutableStateOf("")
        private set
    var playingFromSongsScreen by mutableStateOf(true)
        private set
    val albumSongInfo = mutableListOf<SongInfo>()
    fun updatePlayingFromSongsScreen(state: Boolean) {
        playingFromSongsScreen = state
    }
    fun updateSelectedAlbum(album: String) {
        selectedAlbum = album
    }
    fun updateSongInfoIterator(iteration: Int) {
        songInfoIterator = iteration
    }
    fun incrementSongInfoIterator(increment: Int) {
        songInfoIterator += increment
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