package com.example.audio_player

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.GnssAntennaInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.media3.common.C
import androidx.media3.common.Effect
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSourceEventListener
import com.example.audio_player.ui.theme.Audio_playerTheme
import com.example.audio_player.ui.theme.lcdFont
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<PlayerViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerViewModel(
                        applicationContext
                    ) as T

                }
            }
        }
    )
    lateinit var player: ExoPlayer
    lateinit var myAudioSink: AudioSink
    val spectrumAnalyzer = SpectrumAnalyzer()

    @OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK),
                0
            )
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
            1
        ) } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            )
        }
        val songInfo = mediaStoreSongName(applicationContext)
        val albumInfo = getAlbumList(applicationContext)
        // Audio processor is passed to the audio sink
        myAudioSink = DefaultAudioSink.Builder(applicationContext)
            .setAudioProcessors(arrayOf(spectrumAnalyzer))
            .build()
        val renderersFactory = object : DefaultRenderersFactory(applicationContext) {
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
        player = ExoPlayer.Builder(applicationContext) // Player declaration
            .setRenderersFactory(renderersFactory)
            .build()
        val listener = PlayerListener(applicationContext, viewModel, player) // Do not remove, though unused
        player.addListener(listener)
        enableEdgeToEdge()
        setContent {
            Audio_playerTheme {
                NavHost(player, songInfo, spectrumAnalyzer, viewModel, albumInfo)
                if (viewModel.isPlaying) {
                    LaunchedEffect(Unit) {
                        while (true) {
                            viewModel.updateCurrentSongPosition(player.currentPosition)
                            delay(1.seconds / 30)
                        }
                    }
                    LaunchedEffect(Unit) {
                        while (true) {
                            if (player.duration != C.TIME_UNSET) {
                                viewModel.updateSongDuration(time = player.duration / 1000)
                            }
                            delay(1.seconds / 30)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Intent(AudioPlaybackService.Actions.STOP.toString()).also {
                startService(it)
            }
        } catch (e: IllegalArgumentException) {
        }
    }
    override fun onPause() {
        super.onPause()
        try {
            Intent(AudioPlaybackService.Actions.START.toString()).also {
                startService(it)
            }
        } catch (e: IllegalArgumentException) {
        }
    }

    override fun onDestroy() {// Releases the player
        super.onDestroy()
        player.release()
    }
}

@Composable
fun LcdText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.White,
        fontSize = 15.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 4.sp
    )
}

@Composable
fun LargeLcdText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.White,
        fontSize = 20.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 5.sp
    )
}
@Composable
fun LargePlayerScreenLcdText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.White,
        fontSize = 30.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 5.sp
    )
}
@Composable
fun AlbumScreenLcdText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.White,
        fontSize = 15.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 15.sp
    )
}

