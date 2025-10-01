package com.example.audio_player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.audio_player.ui.theme.Audio_playerTheme
import com.example.audio_player.ui.theme.lcdFont
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

val Context.dataStore by preferencesDataStore(name = "settings")

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<PlayerViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlayerViewModel(
                        applicationContext
                    ) as T
                }
            }
        }
    )

    val mediaSessionService = ForegroundNotificationService()

    @OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //=============================== Permissions ===============================//
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                1
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            )
        }
        //=============================== Media Declarations ===============================//
        var mediaController: MediaController? = null
        val sessionToken = SessionToken(
            this,
            ComponentName(this, mediaSessionService::class.java)
        )
        val controllerFuture = MediaController.Builder(
            this,
            sessionToken
        ).buildAsync()

        controllerFuture.addListener(
            { mediaController = controllerFuture.get() },
            MoreExecutors.directExecutor()
        )

        val songInfo = mediaStoreSongInfo(applicationContext)
        val albumInfo = getAlbumList(applicationContext)
        
        lifecycleScope.launch {
            while (mediaController == null) {
                delay(50)
            }
            val spectrumAnalyzer = mediaSessionService.getSpectrumAnalyzer()
            val listener = PlayerListener(applicationContext, viewModel, mediaController)
            mediaController.addListener(listener)

            enableEdgeToEdge()
            setContent {
                Audio_playerTheme {
                    NavHost(
                        mediaController,
                        songInfo,
                        spectrumAnalyzer,
                        viewModel,
                        albumInfo,
                        applicationContext
                    )
                    if (viewModel.isPlaying) {
                        LaunchedEffect(Unit) {
                            while (true) {
                                mediaController.let { viewModel.updateCurrentSongPosition(it.currentPosition) }
                                delay(1.seconds / 30)
                            }
                        }
                        LaunchedEffect(Unit) {
                            while (true) {
                                mediaController.let {
                                    if (it.duration != C.TIME_UNSET) {
                                        viewModel.updateSongDuration(time = mediaController.duration / 1000)
                                    }
                                }
                                delay(1.seconds / 30)
                            }
                        }
                    }
                }
            }
        }
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
    fun LargePlayerScreenLcdText(
        text: String,
        modifier: Modifier = Modifier,
        viewModel: PlayerViewModel
    ) {
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
    fun AlbumScreenLcdText(
        text: String,
        modifier: Modifier = Modifier,
        viewModel: PlayerViewModel
    ) {
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

