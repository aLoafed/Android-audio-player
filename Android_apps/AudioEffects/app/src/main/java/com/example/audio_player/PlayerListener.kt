package com.example.audio_player

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

class PlayerListener(
    private val applicationContext: Context,
    private val viewModel: PlayerViewModel,
    private val player: ExoPlayer,
) : Player.Listener {

    @OptIn(UnstableApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        viewModel.updateIsPlaying(!viewModel.isPlaying)
        Log.d("Neoplayer","onPlaybackStateChanged")
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(applicationContext, "Unknown error occurred", Toast.LENGTH_LONG).show()
    }
}