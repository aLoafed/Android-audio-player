package com.example.audio_player

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.toColorLong
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.example.audio_player.ui.theme.LcdGrey

@UnstableApi
class ForegroundNotificationService : MediaSessionService() {

    //================== Exposed variables ==================//
    lateinit var player: ExoPlayer
    lateinit var mediaSession: MediaSession
    val spectrumAnalyzer = SpectrumAnalyzer()
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    fun mediaSessionInit(context: Context): MediaSession {
        val mediaSessionCallback = object : MediaSession.Callback{}
        mediaSession = MediaSession.Builder(context, player)
            .setCallback(mediaSessionCallback)
            .build()
        return mediaSession
    }
    fun playerInit(context: Context): ExoPlayer {
        val myAudioSink = DefaultAudioSink.Builder(context)
            .setAudioProcessors(arrayOf(spectrumAnalyzer))
            .build()
        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioRenderers(
                context: Context,
                extensionRendererMode: Int,
                mediaCodecSelector: MediaCodecSelector,
                enableDecoderFallback: Boolean,
                audioSink: AudioSink,
                eventHandler: Handler,
                eventListener: AudioRendererEventListener,
                out: ArrayList<Renderer>
            ) {
                super.buildAudioRenderers(
                    context,
                    extensionRendererMode,
                    mediaCodecSelector,
                    enableDecoderFallback,
                    myAudioSink,
                    eventHandler,
                    eventListener,
                    out
                )
                out.add(
                    MediaCodecAudioRenderer(
                        context,
                        mediaCodecSelector,
                        enableDecoderFallback,
                        eventHandler,
                        eventListener,
                        myAudioSink
                    )
                )
            }
        }
        player = ExoPlayer.Builder(context) // Player declaration
            .setRenderersFactory(renderersFactory)
            .build()
        return player
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        when(intent?.action) {
//            Actions.START.toString() -> start()
//            Actions.STOP.toString() -> stopSelf()
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }
//    @OptIn(UnstableApi::class)
//    fun start() {
////        val notification = NotificationCompat.Builder(this, "audio_channel")
////            .setContentTitle("Track title")
////            .setContentText("Artist - Album")
////            .setSmallIcon(R.mipmap.ic_launcher)
//////            .setLargeIcon(R.mipmap.ic_launcher)
////            .setColor(LcdGrey.toColorLong().toInt())
////            .setSilent(true)
////            .setOngoing(true)
////            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession))
////            .build()
////        startForeground(670, notification)
//    }

    enum class Actions {
        START,STOP
    }
}