package com.example.audio_player

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.C
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
import com.example.audio_player.ui.theme.Audio_playerTheme
import com.example.audio_player.ui.theme.lcdFont
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

val Context.dataStore by preferencesDataStore(name = "settings")
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
    val spectrumAnalyzer = SpectrumAnalyzer()
    //======================= Audio processor is parsed to the audio sink =======================//
    val myAudioSink = DefaultAudioSink.Builder(applicationContext)
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
    val mediaSessionCallback = object : MediaSession.Callback{}
    val player = ExoPlayer.Builder(applicationContext) // Player declaration
        .setRenderersFactory(renderersFactory)
        .build()
    val mediaSession = MediaSession.Builder(applicationContext, player)
        .setId("media_session")
        .setCallback(mediaSessionCallback)
        .build()

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
        val songInfo = mediaStoreSongInfo(applicationContext)
        val albumInfo = getAlbumList(applicationContext)
        

        val listener = PlayerListener(applicationContext, viewModel, player) // Do not remove, though unused
        player.addListener(listener)
        enableEdgeToEdge()
        setContent {
            Audio_playerTheme {
                NavHost(player, songInfo, spectrumAnalyzer, viewModel, albumInfo, applicationContext)
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
            Intent(ForegroundNotificationService.Actions.STOP.toString()).also {
                startService(it)
            }
        } catch (e: IllegalArgumentException) {
        }
    }

    override fun onStop() {
        super.onStop()
        val intent = Intent(this, ForegroundNotificationService(mediaSession, player, viewModel)::class.java).apply { // Explicit intent to start notification
            action = ForegroundNotificationService.Actions.START.toString()
        }
        startService(intent)
    }

    override fun onDestroy() {// Releases the player
        super.onDestroy()
        player.release()
    }
}

@Composable
fun LcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = text,
        color = viewModel.textColor,
        fontSize = 15.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 4.sp
    )
}

@Composable
fun LargeLcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = text,
        color = viewModel.textColor,
        fontSize = 20.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 17.sp
    )
}
@Composable
fun LargePlayerScreenLcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = text,
        color = viewModel.textColor,
        fontSize = 30.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
//        lineHeight = 5.sp
    )
}
@Composable
fun AlbumScreenLcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = text,
        color = viewModel.textColor,
        fontSize = 15.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 15.sp
    )
}

//@Preview
//@Composable
//fun PreviewEQIcon() {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .size(260.dp, 140.dp)
//                .padding(horizontal = 15.dp),
//            verticalAlignment = Alignment.Bottom,
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            val heights = listOf(18,14,26,20,34,12,6)
//            for (i in heights) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .width(25.dp),
//                    verticalArrangement = Arrangement.SpaceBetween,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy((-2).dp),
//                        verticalAlignment = Alignment.Bottom
//                    ) {
//                        Text(
//                            modifier = Modifier,
//                            text = textLevelBuilder(1..i),
//                            fontFamily = dotoFamily,
//                            fontWeight = FontWeight.W500,
//                            fontSize = 23.sp,
//                            color = Color.White,
//                            letterSpacing = 0.sp,
//                            lineHeight = 3.sp,
//                            textAlign = TextAlign.Center
//                        )
//                        Text(
//                            modifier = Modifier,
//                            text = textLevelBuilder(1..i),
//                            fontFamily = dotoFamily,
//                            fontWeight = FontWeight.W500,
//                            fontSize = 23.sp,
//                            color = Color.White,
//                            letterSpacing = 0.sp,
//                            lineHeight = 3.sp,
//                            textAlign = TextAlign.Center
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

