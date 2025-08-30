package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.audio_player.ui.theme.dotoFamily
import java.nio.file.WatchEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(player: ExoPlayer, spectrumAnalyzer: SpectrumAnalyzer, viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .height(30.dp)
        )
        Image( // Album art
            bitmap = (
                    if (viewModel.playingFromSongsScreen) {
                        songInfo[viewModel.songInfoIterator].albumArt
                    } else {
                        viewModel.albumSongInfo[viewModel.songInfoIterator].albumArt
                    }
                    ),
            modifier = Modifier
                .size(225.dp),
            contentDescription = null
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        LargePlayerScreenLcdText(
            if (viewModel.playingFromSongsScreen) {
                songInfo[viewModel.songInfoIterator].name
            } else {
                viewModel.albumSongInfo[viewModel.songInfoIterator].name
            }
        )
        Spacer(
            modifier = Modifier
                .height(5.dp)
        )
        LargeLcdText(
            if (viewModel.playingFromSongsScreen) {
                songInfo[viewModel.songInfoIterator].artist
            } else {
                viewModel.albumSongInfo[viewModel.songInfoIterator].artist
            }
        )
        LargeLcdText(
            if (viewModel.playingFromSongsScreen) {
                songInfo[viewModel.songInfoIterator].album
            } else {
                viewModel.albumSongInfo[viewModel.songInfoIterator].album
            }
        )
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        Row( // Playback controls
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton( // Previous button
                modifier = Modifier
                    .size(80.dp),
                onClick = {
                    if (player.currentPosition < 10000L) {
                        player.seekToPreviousMediaItem()
                        viewModel.incrementSongInfoIterator(-1)
                    } else {
                        player.seekTo(0L)
                    }
                },
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                content = {
                    Icon(
                        painter = painterResource(R.drawable.skip_previous),
                        contentDescription = null
                    )
                },
            )
            IconButton( // Play & pause button
                onClick = {
                    if (player.isPlaying) {
                        player.pause()
                        viewModel.updateIsPlaying(false)
                    } else {
                        player.play()
                        viewModel.updateIsPlaying(true)
                    }
                },
                modifier = Modifier
                    .size(80.dp),
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                content = {
                    Icon(
                        painter = (
                            if (!viewModel.isPlaying) {
                                painterResource(R.drawable.large_play_arrow)
                            } else {
                                painterResource(R.drawable.pause)
                            }
                        ),
                        contentDescription = null
                    )
                }
            )
            IconButton( // Skip button
                modifier = Modifier
                    .size(80.dp),
                onClick = {
                    player.seekToNextMediaItem()
                    player.play()
                    viewModel.incrementSongInfoIterator(1)
                },
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                content = {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = null
                    )
                },
            )
        }
        GraphicalEqualizer(spectrumAnalyzer, viewModel)
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
        )
        SeekBar(player, viewModel)
    }
}
@OptIn(UnstableApi::class)
@Composable
fun GraphicalEqualizer(spectrumAnalyzer: SpectrumAnalyzer, viewModel: PlayerViewModel) {
    Row(
        modifier = Modifier
            .size(280.dp, 140.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val fieldName = listOf("63","160","400","1k","2.5k","6.3k","16k")
        for (i in 0..6) {
            AudioLevelColumn(fieldName[i],spectrumAnalyzer, viewModel)
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun AudioLevelColumn(fieldName: String, spectrumAnalyzer: SpectrumAnalyzer, viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(35.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DotoText(
            fieldName,
            Modifier
                .offset(y = 20.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy((-2).dp)
        ) {
            val tick = viewModel.currentSongPosition
            if (viewModel.isPlaying) {
                AudioLevelText(fieldName, spectrumAnalyzer, tick)
                AudioLevelText(fieldName, spectrumAnalyzer, tick)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun AudioLevelText(fieldName: String, spectrumAnalyzer: SpectrumAnalyzer, tick: Float) {
    val eqTransition = rememberInfiniteTransition()
    val target = remember(tick) {
        if (spectrumAnalyzer.eqList.count() != 0) {
            when (fieldName) {
                "63" -> level63(spectrumAnalyzer)
                "160" -> level160(spectrumAnalyzer)
                "400" -> level400(spectrumAnalyzer)
                "1k" -> level1k(spectrumAnalyzer)
                "2.5k" -> level2500k(spectrumAnalyzer)
                "6.3k" -> level6300k(spectrumAnalyzer)
                "16k" -> level16k(spectrumAnalyzer)
                else -> 0f
            }
        } else {
            0f
        }
    }
    val levels by eqTransition.animateFloat(
        initialValue = 0f,
        targetValue = target,
        animationSpec = infiniteRepeatable(
            tween(
                10,
                0,
                EaseOut
            ),
            repeatMode = RepeatMode.Reverse
        )
    )
    Text(
        modifier = Modifier,
        text = textLevelBuilder(1..levels.toInt()),
        fontFamily = dotoFamily,
        fontWeight = FontWeight.W100,
        fontSize = 23.sp,
        color = Color.White,
        letterSpacing = 0.sp,
        lineHeight = 3.sp,
        textAlign = TextAlign.Center
        )
}

@OptIn(UnstableApi::class)
fun level63(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[0]
    if (tempValue > 110.0) {
       tempValue = 110.0
    }
    tempValue = tempValue / 5.5 * 2
    return tempValue.toFloat()
}
@OptIn(UnstableApi::class)
fun level160(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[1]
    if (tempValue > 55.0) {
        tempValue = 55.0
    }
    tempValue = tempValue / 2.75 * 2
    return tempValue.toFloat()
}
@OptIn(UnstableApi::class)
fun level400(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[2]
    if (tempValue > 40.0) {
        tempValue = 40.0
    }
    tempValue = tempValue / 2.0 * 2
    return tempValue.toFloat()
}
@OptIn(UnstableApi::class)
fun level1k(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[3]
    if (tempValue > 13.0) {
        tempValue = 13.0
    }
    tempValue = tempValue / 0.65 * 2
    return tempValue.toFloat()
}
@OptIn(UnstableApi::class)
fun level2500k(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[4]
    if (tempValue > 5.0) {
        tempValue = 5.0
    }
    tempValue = tempValue / 0.25 * 2
    return tempValue.toFloat()
}
@OptIn(UnstableApi::class)
fun level6300k(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[5]
    if (tempValue > 1.5) {
        tempValue = 1.5
    }
    tempValue = tempValue / 0.075 * 2
    return tempValue.toFloat()
}
@OptIn(UnstableApi::class)
fun level16k(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tempValue = spectrumAnalyzer.eqList[6]
    if (tempValue > 1.5) {
        tempValue = 1.5
    }
    tempValue = tempValue / 0.075 * 2
    return tempValue.toFloat()
}
@Composable
fun DotoText(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        fontFamily = dotoFamily,
        fontWeight = FontWeight.W300,
        fontSize = 8.sp,
        color = MaterialTheme.colorScheme.secondary,
    )
}
fun textLevelBuilder(n: IntRange): String {
    var tempText = ""
    for (i in n) {
        tempText += "_\n"
    }
    return tempText
}
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(player: ExoPlayer, viewModel: PlayerViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var currentSongPosition by remember { mutableFloatStateOf(viewModel.currentSongPosition) }
        LcdText(
            "${(viewModel.currentSongPosition / 60).toInt()}:${(viewModel.currentSongPosition % 60).toInt()}"
        )
        Slider(
            value = viewModel.currentSongPosition,
            valueRange = 0f..viewModel.duration,
            modifier = Modifier
                .size(250.dp, 40.dp),
            onValueChange = {
                currentSongPosition = it
                viewModel.updateSongPosition(player, currentSongPosition.toLong())
                viewModel.updateIsPlaying(true)
            },
            thumb = {
                SliderThumb()
            },
            track = {
                SliderTrack()
            },
        )
        LcdText(
            "${(viewModel.duration / 60).toInt()}:${(viewModel.duration % 60).toInt()}"
        )
    }
}
@Composable
fun SliderThumb() {
    Column(
        modifier = Modifier
            .size(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(
            modifier = Modifier,
            onDraw = {
                drawCircle(
                    color = Color.White,
                    radius = 25f,
                    center = this.center,
                    style = Fill
                )
            }
        )
    }
}
@Composable
fun SliderTrack() {
    Column(
        modifier = Modifier
            .height(15.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Canvas(
            modifier = Modifier,
            onDraw = {
                drawRoundRect(
                    size = Size(600f, 15f),
                    style = Fill,
                    color = Color(0.74f,0.74f,0.74f),
                    cornerRadius = CornerRadius(10f,10f),
                    topLeft = Offset(0f,-6.5f)
                )
            }
        )
    }
}