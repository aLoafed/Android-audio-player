package com.example.audio_player

import android.content.Context
import android.os.Handler
import androidx.media3.common.util.Log
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

@UnstableApi
class ForegroundNotificationService : MediaSessionService() {
    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Log.d("Neoplayer", "Media session is grabbed")
        return mediaSession
    }

    override fun onCreate() {
        Log.d("Neoplayer", "Service onCreate hit")
        super.onCreate()
        val myAudioSink = DefaultAudioSink.Builder(this)
            .setAudioProcessors(arrayOf(SpectrumAnalyzer()))
            .build()
        val renderersFactory = object : DefaultRenderersFactory(this) {
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
        player = ExoPlayer.Builder(this) // Player declaration
            .setRenderersFactory(renderersFactory)
            .build()
//        val mediaSessionCallback = object : MediaSession.Callback{}
        mediaSession = MediaSession.Builder(this, player)
            .build()
        Log.d("Neoplayer", "Media session created")
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
            Log.d("Neoplayer", "Service onDestroy hit")
        }
        super.onDestroy()
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
//        val notification = NotificationCompat.Builder(this, "audio_channel")
//            .setContentTitle("Track title")
//            .setContentText("Artist - Album")
//            .setSmallIcon(R.mipmap.ic_launcher)
////            .setLargeIcon(R.mipmap.ic_launcher)
//            .setColor(LcdGrey.toColorLong().toInt())
//            .setSilent(true)
//            .setOngoing(true)
//            .setStyle(MediaStyleNotificationHelper.MediaStyle(mediaSession))
//            .build()
//        startForeground(670, notification)
//    }
//
//    enum class Actions {
//        START,STOP
//    }
}