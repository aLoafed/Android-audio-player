package com.example.audio_player

import android.Manifest
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
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
        viewModel.initViewModel(applicationContext)
        var mediaController: MediaController? = null
        val controllerFuture = MediaController.Builder(
            this,
            SessionToken(
                this,
                ComponentName(this, mediaSessionService::class.java)
            )
        ).buildAsync()
        controllerFuture.addListener(
            { mediaController = controllerFuture.get() },
            MoreExecutors.directExecutor()
        )
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

        val songInfo = mediaStoreSongInfo(applicationContext)
        val albumInfo = getAlbumList(applicationContext)
        
        lifecycleScope.launch {
            val spectrumAnalyzer = mediaSessionService.getSpectrumAnalyzer()
            enableEdgeToEdge()
            setContent {
                if (viewModel.showBasicLoadingScreen) {
                    BasicLoadingScreen(viewModel)
                } else {
                    DetailedLoadingScreen(viewModel)
                }
            }
            while (mediaController == null) {
                delay(50)
            }
            while (!viewModel.loadingFinished) {
                delay(5)
            }
            val listener = PlayerListener(applicationContext, viewModel, mediaController)
            mediaController.addListener(listener)
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
fun ExtraLargeLcdText(
    text: String,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        modifier = modifier,
        text = text,
        color = viewModel.textColor,
        fontSize = 30.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        style = style
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
//fun PreviewLoadingParallelogram() {
//    Canvas(
//        modifier = Modifier
//            .size(30.dp)
//    ) {
//        val path = Path()
//        path.moveTo(10f,50f)
//        path.relativeLineTo(0f,15f)
//        path.relativeLineTo(45f,-30f)
//        path.relativeLineTo(0f,-15f)
//        path.close()
//
//        drawPath(
//            path = path,
//            color = LcdOrange,
//            style = Fill
//        )
//    }
//}