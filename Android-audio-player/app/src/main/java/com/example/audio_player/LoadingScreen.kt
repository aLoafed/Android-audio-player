package com.example.audio_player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.LcdGrey

@Composable
fun LoadingScreen() {
    var flippedLoadingColumnRange by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        flippedLoadingColumnRange = 5
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(LcdGrey),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        val flippedLoadingColumn by animateIntAsState(
            targetValue = flippedLoadingColumnRange,
            animationSpec = TweenSpec(
                durationMillis = 1000,
                easing = LinearEasing,
            ),
        )
        for (i in 1..flippedLoadingColumn) {
            when (i) {
                3 -> {
                    Spacer(Modifier.width(15.dp))
                    FlippedLoadingColumn()
                }
                5 -> {
                    FlippedLoadingColumn()
                }
                else -> {
                    LoadingColumn()
                    ConnectingBar()
                }
            }
        }
    }
}
@Composable
fun ConnectingBar() {
    Canvas(
        modifier = Modifier
            .width(15.dp)
            .fillMaxHeight()
    ) {
        drawRect(
            color = LcdBlueWhite,
            size = Size(1f, this.size.height)
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
            .width(60.dp)
            .padding(vertical = 30.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        val loadingParellelogram by animateIntAsState(
            targetValue = loadingLevelRange,
            animationSpec = TweenSpec(
                durationMillis = 200,
                easing = LinearEasing,
            ),
        )
        for (i in 1..loadingParellelogram) {
            LoadingParallelogram()
        }
    }
}
@Composable
fun FlippedLoadingColumn() {
    var loadingLevelRange by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        loadingLevelRange = 15
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .padding(vertical = 30.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        val loadingParellelogram by animateIntAsState(
            targetValue = loadingLevelRange,
            animationSpec = TweenSpec(
                durationMillis = 200,
                easing = LinearEasing,
            ),
        )
        for (i in 1..loadingParellelogram) {
            FlippedLoadingParallelogram()
        }
    }
}

@Composable
fun LoadingParallelogram() {
    val factor = 2.5f
    Canvas(
        modifier = Modifier
            .size(40.dp)
    ) {
        val path = Path()
        path.moveTo(10f * factor,50f * factor)
        path.relativeLineTo(0f * factor,17f * factor) // Lower left
        path.relativeLineTo(45f * factor,-30f * factor) // Lower right
        path.relativeLineTo(0f * factor,-17f * factor) // Upper right
        path.close() // Upper left

        drawPath(
            path = path,
            color = LcdBlueWhite,
            style = Stroke(
                width = 1f
            )
        )
    }
}
@Composable
fun FlippedLoadingParallelogram() {
    val factor = 2.5f
    Canvas(
        modifier = Modifier
            .size(40.dp)
    ) {
        val path = Path()
        path.moveTo(10f * factor,20f * factor)
        path.relativeLineTo(0f * factor,17f * factor) // Lower left
        path.relativeLineTo(45f * factor,30f * factor) // Lower right
        path.relativeLineTo(0f * factor,-17f * factor) // Upper right
        path.close() // Upper left

        drawPath(
            path = path,
            color = LcdBlueWhite,
            style = Fill
        )
    }
}