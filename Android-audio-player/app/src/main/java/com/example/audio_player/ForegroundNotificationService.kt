package com.example.audio_player

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toColorLong
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.audio_player.ui.theme.LcdGrey
import com.example.audio_player.ui.theme.LcdOrange

class ForegroundNotificationService(
    val mediaSession: MediaSession,
    val player: ExoPlayer,
    val viewModel: PlayerViewModel
): Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @OptIn(UnstableApi::class)
    private fun start() {
        val art by mutableStateOf(BitmapFactory.decodeByteArray(player.mediaMetadata.artworkData, 0, 500))
        val title by mutableStateOf(player.mediaMetadata.title)
        val notification = NotificationCompat.Builder(this, "audio_channel")
            .setContentTitle(title)
            .setContentText("Artist - Album")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(art)
            .setColor(LcdGrey.toColorLong().toInt())
            .setSilent(true)
            .setOngoing(true)
            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession))
            .build()
        startForeground(670, notification)
    }

    enum class Actions {
        START,STOP
    }
}