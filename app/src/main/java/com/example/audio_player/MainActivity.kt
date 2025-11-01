package com.example.audio_player

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
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
import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sin
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
    val mediaSessionService = PlayerService()

    @ExperimentalFoundationApi
    @OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets the settings' variables from the json
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
        val songInfo = mediaStoreSongInfo(applicationContext)
        val albumInfo = getAlbumList(applicationContext)
        
        lifecycleScope.launch {
            val spectrumAnalyzer = mediaSessionService.getSpectrumAnalyzer()
            spectrumAnalyzer.equaliserIsOn = true
            enableEdgeToEdge()
            setContent {
                if (viewModel.showBasicLoadingScreen) {
                    BasicLoadingScreen(viewModel)
                } else {
                    DetailedLoadingScreen(viewModel)
                }
                val dialogQueue = viewModel.visiblePermissionDialogQueue
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

    override fun onStop() {
        super.onStop()
        val spectrumAnalyzer = mediaSessionService.getSpectrumAnalyzer()
        spectrumAnalyzer.equaliserIsOn = false
    }

    override fun onResume() {
        super.onResume()
        val spectrumAnalyzer = mediaSessionService.getSpectrumAnalyzer()
        spectrumAnalyzer.equaliserIsOn = true
    }
}

//@Composable
//fun RequestPermissions(applicationContext: Context, ) {
//    val permissionRequestLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (!isGranted) {
//
//        }
//    }
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Permissions
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//            0
//        )
//    }
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK),
//            0
//        )
//    }
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//        when {
//            ContextCompat.checkSelfPermission(
//                applicationContext,
//                Manifest.permission.READ_MEDIA_AUDIO
//            ) == PackageManager.PERMISSION_GRANTED -> {
//
//            }
//        }
//    } else {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//            2
//        )
//    }
//}

@Composable
fun LcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = if (text.length > 34) {
            "${text.removeRange(34 until text.length)}..."
        } else {
            text
        },
        color = viewModel.textColor,
        fontSize = 15.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = 4.sp
    )
}

@Composable
fun LargeLcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel, lineHeight: TextUnit = 17.sp) {
    Text(
        modifier = modifier,
        text = text,
        color = viewModel.textColor,
        fontSize = 20.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        lineHeight = lineHeight
    )
}

@Composable
fun PlayerLargeLcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = if (text.length > 30) {
            "${text.removeRange(34 until text.length)}..."
        } else {
            text
        },
        color = viewModel.textColor,
        fontSize = 25.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun PlayerLcdText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = if (text.length > 34) {
            "${text.removeRange(34 until text.length)}..."
        } else {
            text
        },
        color = viewModel.textColor,
        fontSize = 20.sp,
        fontFamily = lcdFont,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
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

fun Color.increaseBrightness(brightness: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] += brightness
    return Color(ColorUtils.HSLToColor(hsl))
}

//@Preview
//@Composable
//fun TmpIconPreview() {
//    Canvas(
//        modifier = Modifier
//            .size(450.dp)
//            .background(Color(0xFF000000))
//    ) {
//        val path = Path()
//        path.moveTo(200f, 500f)
//        val sigmaWidth = 10f
//        for (i in 0..10000) {
//            val x = i.toFloat() * 10 + 200f
////            val amplitude = E.pow(-1 * (x.pow(2)/sigmaWidth.pow(2)).toDouble()) // y = e^(-x²/σ²) · sin(ωx)
//            val amplitude = E.pow(-x.toDouble()) // y = e^(-|x|) · sin(ωx)
//            path.lineTo(
//                x,
//                (10000 * amplitude * sin(3 * x)).toFloat() + 500f
//                )
//        }
//        drawPath(
//            path = path,
//            color = Color.White
//        )
//    }
//}