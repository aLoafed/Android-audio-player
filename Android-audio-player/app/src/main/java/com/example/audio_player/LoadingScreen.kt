package com.example.audio_player

import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutElastic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.LcdGrey
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(vertical = 10.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        ExtraLargeLcdText(
            "Loading:",
            Modifier,
            viewModel,
            TextStyle(
                shadow = Shadow(
                    color = viewModel.textColor.copy(alpha = 0.8f),
                    offset = Offset(0f,0f),
                    blurRadius = 20f
                )
            )
        )
        Spacer(Modifier.height(10.dp))
        LoadingBars(viewModel)
    }
}

@Composable
fun LoadingBars(viewModel: PlayerViewModel) {
    var flippedLoadingColumnRange by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        flippedLoadingColumnRange = 5
    }

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        val flippedLoadingColumn by animateIntAsState(
            targetValue = flippedLoadingColumnRange,
            animationSpec = TweenSpec(
                durationMillis = 500,
                easing = LinearEasing,
            ),
        )
        for (i in 1..flippedLoadingColumn) {
            when (i) {
                2 -> {
                    LoadingColumn()
                    MultiConnectingBar()
                }
                3 -> {
                    Spacer(Modifier.width(15.dp))
                    FlippedLoadingColumn()
                }
                4 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(60.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        FourthConnectingBar()
                        LoadingColumn()
                    }
                }
                5 -> {
                    Spacer(Modifier.width(31.dp))
                    FlippedLoadingColumn(offset = 24.dp)
                    LaunchedEffect(Unit) {
                        delay(550)
                        viewModel.updateFinishedLoading(true)
                    }
                }
                else -> {
                    LoadingColumn()
                    FirstConnectingBar()
                }
            }
        }
    }
}
@Composable
fun FourthConnectingBar() {
    var brightnessLevel by remember { mutableFloatStateOf(0f) }
    val path = Path()
    path.moveTo(96f,-85f)
    for (i in 1..5) {
        joinPath(path)
    }
    path.moveTo(0f,160f)
    for (i in 1..5) {
        barToConnector(path)
    }
    LaunchedEffect(Unit) {
        brightnessLevel = 1f
    }
    val brightness by animateFloatAsState(
        targetValue = brightnessLevel,
        animationSpec = TweenSpec(
            500,
            easing = EaseOutElastic
        )
    )
    Canvas(
        modifier = Modifier
            .size(5.dp, 650.dp)
            .offset(x = 15.dp, y = 55.dp),
    ) {
        drawRect(
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            size = Size(1f, this.size.height - 285f),
        )
        drawPath(
            path = path,
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            style = Stroke(
                1f,
            )
        )
    }
}
fun joinPath(path: Path) {
    path.relativeLineTo(40f,0f)
    path.relativeLineTo(0f,115f)
    path.relativeLineTo(-40f, 0f)
    path.relativeMoveTo(40f, -47f)
    path.relativeLineTo(102f,0f)
    path.relativeMoveTo(-142f,160f) // 47f
}
fun barToConnector(path: Path) {
    path.relativeLineTo(98f, -65.3f)
    path.relativeLineTo(140f, 0f)
    path.relativeMoveTo(-238f, 291.3f)
}
@Composable
fun FirstConnectingBar() {
    var brightnessLevel by remember { mutableFloatStateOf(0f) }
    val path = Path()
    path.moveTo(0f, 1727f)
    path.relativeLineTo(86f,0f)

    LaunchedEffect(Unit) {
        brightnessLevel = 1f
    }
    val brightness by animateFloatAsState(
        targetValue = brightnessLevel,
        animationSpec = TweenSpec(
            500,
            easing = EaseOutElastic
        )
    )
    Canvas(
        modifier = Modifier
            .size(5.dp, 650.dp)
            .offset(x = (-17).dp),
    ) {
        drawRect(
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            size = Size(1f, this.size.height - 100f),
        )
//        drawRoundRect(
//            color = changeBrightness(LcdBlueWhite.toArgb(), brightness).copy(alpha = 0.8f),
//            size = Size(1f, this.size.height - 50f),
//            style = Stroke(
//                4f
//            ),
//            cornerRadius = CornerRadius(20f,20f)
//        )
        drawPath(
            path = path,
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            style = Stroke(
                1f,
            )
        )
    }
}
@Composable
fun MultiConnectingBar() {
    var brightnessLevel by remember { mutableFloatStateOf(0f) }
    val path = Path()
    path.moveTo(18f,75f)
    for (i in 1..15) {
        path.relativeLineTo(112f, 0f)
        path.relativeMoveTo(-112f, 0f)
        path.relativeMoveTo(0f, 113f)
    }
    LaunchedEffect(Unit) {
        brightnessLevel = 1f
    }

    val brightness by animateFloatAsState(
        targetValue = brightnessLevel,
        animationSpec = TweenSpec(
            500,
            easing = EaseOutElastic
        )
    )
    Canvas(
        modifier = Modifier
            .size(5.dp, 650.dp)
            .offset(x = (-17).dp)
    ) {
        drawRect(
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            size = Size(1f, this.size.height)
        )
        drawPath(
            path = path,
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            style = Stroke(
                1f
            )
        )
    }
}

@Composable
fun LoadingColumn() {
    var loadingLevelRange by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        loadingLevelRange = 15
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        val loadingParellelogram by animateIntAsState(
            targetValue = loadingLevelRange,
            animationSpec = TweenSpec(
                durationMillis = 100,
                easing = LinearEasing,
            ),
        )
        for (i in 1..loadingParellelogram) {
            LoadingParallelogram(i)
        }
    }
}
@Composable
fun FlippedLoadingColumn(offset: Dp = 0.dp) {
    var loadingLevelRange by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        loadingLevelRange = 15
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .offset(y = offset),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        val loadingParellelogram by animateIntAsState(
            targetValue = loadingLevelRange,
            animationSpec = TweenSpec(
                durationMillis = 100,
                easing = LinearEasing,
            ),
        )
        for (i in 1..loadingParellelogram) {
            FlippedLoadingParallelogram(i)
        }
    }
}

@Composable
fun LoadingParallelogram(index: Int) {
    var brightnessLevel by remember { mutableFloatStateOf(0f) }
    val factor = 2.5f

    LaunchedEffect(Unit) {
        brightnessLevel = 1f
    }

    val brightness by animateFloatAsState(
        targetValue = brightnessLevel,
        animationSpec = TweenSpec(
            400,
            easing = EaseOutBounce,
            delay = index * 10
        )
    )
    Canvas(
        modifier = Modifier
            .size(40.dp)
    ) {
        val mainPath = Path()
        parallelogramPath(mainPath, 2.5f)
        val secondaryPath = Path()
        parallelogramPath(secondaryPath, 0.6f)
        val tertiaryPath = Path()
        parallelogramPath(tertiaryPath, 0.55f)

        drawPath(
            path = mainPath,
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            style = Fill
        )
        drawPath(
            path = secondaryPath,
            color = LcdGrey,
            style = Stroke(
                3f
            )
        )
        drawPath(
            path = tertiaryPath,
            color = Color.White,
            style = Fill
        )
    }
}
@Composable
fun FlippedLoadingParallelogram(index: Int) {
    var brightnessLevel by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        brightnessLevel = 1f
    }

    val brightness by animateFloatAsState(
        targetValue = brightnessLevel,
        animationSpec = TweenSpec(
            400,
            easing = EaseOutBounce,
            delay = index * 10
        )
    )
    Canvas(
        modifier = Modifier
            .size(40.dp)
    ) {
        val mainPath = Path()
        flippedParallelogramPath(mainPath, 2.5f)
        val secondaryPath = Path()
        flippedParallelogramPath(secondaryPath, 0.6f)
        val tertiaryPath = Path()
        flippedParallelogramPath(tertiaryPath, 0.55f)
        drawPath(
            path = mainPath,
            color = changeBrightness(LcdBlueWhite.toArgb(), brightness),
            style = Stroke(
                1f
            )
        )
        drawPath(
            path = secondaryPath,
            color = LcdGrey,
            style = Stroke(
                2f
            )
        )
        drawPath(
            path = tertiaryPath,
            color = Color.White,
            style = Fill
        )
    }
}
fun flippedParallelogramPath(path: Path, factor: Float) {
    path.moveTo(10f * 2.5f,20f * 2.5f)
    path.relativeLineTo(0f * factor,17f * factor) // Lower left
    path.relativeLineTo(45f * factor,30f * factor) // Lower right
    path.relativeLineTo(0f * factor,-17f * factor) // Upper right
    path.close() // Upper left
}
fun parallelogramPath(path: Path, factor: Float) {
    path.moveTo(10f * 2.5f,50f * 2.5f)
    path.relativeLineTo(0f * factor,17f * factor) // Lower left
    path.relativeLineTo(45f * factor,-30f * factor) // Lower right
    path.relativeLineTo(0f * factor,-17f * factor) // Upper right
    path.close() // Upper left
}

fun changeBrightness(color: Int, factor: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color, hsl)
    hsl[2] *= factor
    return Color(ColorUtils.HSLToColor(hsl))
}