package com.example.audio_player

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioPlaybackService: Service() {
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

    private fun start() {
        val notification = NotificationCompat.Builder(this, "audio_channel")
            .setSmallIcon(R.drawable.library_music)
            .setContentTitle("Music is playing")
            .build()
        startForeground(1, notification)
    }

    enum class Actions {
        START,STOP
    }
}