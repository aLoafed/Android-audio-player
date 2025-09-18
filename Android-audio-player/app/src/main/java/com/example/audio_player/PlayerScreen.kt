package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.audio_player.ui.theme.LcdBlueWhite
import com.example.audio_player.ui.theme.dotoFamily
var shuffleSongInfo = listOf<SongInfo>()
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(player: ExoPlayer, spectrumAnalyzer: SpectrumAnalyzer, viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        PlayingMediaInfo(viewModel, songInfo)
        PlaybackControls(player, viewModel, songInfo)
        GraphicalEqualizer(spectrumAnalyzer, viewModel)
        RepeatShuffleControls(viewModel, player, songInfo)
        SeekBar(player, viewModel)
    }
}
@Composable
fun PlayingMediaInfo(viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
    Image( // Album art
        bitmap = (
                if (viewModel.shuffleMode) {
                    shuffleSongInfo[viewModel.songIterator].albumArt
                } else if (viewModel.playingFromSongsScreen) {
                    songInfo[viewModel.songIterator].albumArt
                } else {
                    viewModel.albumSongInfo[viewModel.songIterator].albumArt
                }
                ),
        modifier = Modifier
            .size(300.dp),
        contentDescription = null
    )
    Spacer(
        modifier = Modifier
            .height(10.dp)
    )
    LargePlayerScreenLcdText(
        if (viewModel.shuffleMode) {
            shuffleSongInfo[viewModel.songIterator].name
        } else if (viewModel.playingFromSongsScreen) {
            songInfo[viewModel.songIterator].name
        } else {
            viewModel.albumSongInfo[viewModel.songIterator].name
        },
        viewModel = viewModel
    )
    Spacer(
        modifier = Modifier
            .height(5.dp)
    )
    LargeLcdText(
        if (viewModel.shuffleMode) {
            shuffleSongInfo[viewModel.songIterator].artist
        } else if (viewModel.playingFromSongsScreen) {
            songInfo[viewModel.songIterator].artist
        } else {
            viewModel.albumSongInfo[viewModel.songIterator].artist
        },
        viewModel = viewModel
    )
    LargeLcdText(
        if (viewModel.shuffleMode) {
            shuffleSongInfo[viewModel.songIterator].album
        } else if (viewModel.playingFromSongsScreen) {
            songInfo[viewModel.songIterator].album
        } else {
            viewModel.albumSongInfo[viewModel.songIterator].album
        },
        viewModel = viewModel
    )
}
@Composable
fun PlaybackControls(player: ExoPlayer, viewModel: PlayerViewModel, songInfo: List<SongInfo>){
    Row( // Playback controls
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(
            // Previous button
            modifier = Modifier
                .size(60.dp),
            onClick = {
                try {
                    if (player.currentPosition < 10000L) {
                        if (player.hasPreviousMediaItem()) {
                            player.seekToPreviousMediaItem()
                        }
                    } else {
                        player.seekTo(0L)
                    }
                } catch (e: IndexOutOfBoundsException) {
                }
            },
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = viewModel.iconColor,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            content = {
                Icon(
                    painter = painterResource(R.drawable.skip_previous),
                    contentDescription = "Previous"
                )
            },
        )
        IconButton( // Play & pause button
            onClick = {
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            },
            modifier = Modifier
                .size(60.dp),
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = viewModel.iconColor,
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
                    contentDescription = "Play & pause"
                )
            }
        )
        IconButton(
            // Skip button
            modifier = Modifier
                .size(60.dp),
            onClick = {
                if (player.hasNextMediaItem()) {
                    player.seekToNextMediaItem()
                }
            },
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = viewModel.iconColor,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            content = {
                Icon(
                    painter = painterResource(R.drawable.skip_next),
                    contentDescription = "Next"
                )
            },
        )
    }
}
@Composable
fun RepeatShuffleControls(viewModel: PlayerViewModel, player: ExoPlayer, songInfo: List<SongInfo>) {
    val tmpSongInfo = mutableListOf<SongInfo>()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton( // Repeat controls
            onClick = {
                when (viewModel.repeatMode) {
                    "normal" -> {
                        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                        viewModel.updateRepeatMode("repeatQueue")
                    }
                    "repeatQueue" -> {
                        player.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                        viewModel.updateRepeatMode("repeatSong")
                    }
                    "repeatSong" -> {
                        player.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                        viewModel.updateRepeatMode("normal")
                    }
                }
            },
            modifier = Modifier
                .size(40.dp),
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = viewModel.iconColor,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            content = {
                Icon(
                    painter = (
                            when (viewModel.repeatMode) {
                                "normal" -> painterResource(R.drawable.repeat) // R.drawable.repeat
                                "repeatQueue" -> painterResource(R.drawable.repeat_on) // R.drawable.repeat_on
                                "repeatSong" -> painterResource(R.drawable.repeat_one_on) // R.drawable.repeat_one_on
                                else -> painterResource(R.drawable.repeat)
                            }
                            ),
                    contentDescription = "Repeat controls"
                )
            }
        )
        IconButton( // Shuffle controls
            onClick = {
                if (!viewModel.shuffleMode) { // Switching to shuffle
                    if (!viewModel.playingFromSongsScreen) { // Playing from albums
                        val tmpShuffledAlbumSongInfo = viewModel.albumSongInfo.shuffled()
                        tmpSongInfo.clear()
                        player.clearMediaItems()
                        for (i in tmpShuffledAlbumSongInfo) {
                            tmpSongInfo.add(i)
                        }
                        shuffleSongInfo = tmpSongInfo
                        for (i in shuffleSongInfo) {
                            player.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    } else { // Playing from songs screen
                        val tmpShuffledSongInfo = songInfo.shuffled()
                        tmpSongInfo.clear()
                        player.clearMediaItems()
                        for (i in tmpShuffledSongInfo) {
                            tmpSongInfo.add(i)
                        }
                        shuffleSongInfo = tmpSongInfo
                        for (i in shuffleSongInfo) {
                            player.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    }
                    viewModel.updateQueuedSongs(shuffleSongInfo)
                    viewModel.updateLastPlayedUnshuffledSong()
                    viewModel.updateSongIterator(0)
                    player.prepare()
                    player.play()
                } else { // Switching to normal playback
                    viewModel.updateSongIterator(viewModel.lastPlayedUnshuffledSong)
                    player.clearMediaItems()
                    if (viewModel.playingFromSongsScreen) { // Playing from songs screen
                        viewModel.updateQueuedSongs(songInfo)
                        for (i in songInfo) {
                            player.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    } else { // Playing from albums screen
                        viewModel.updateQueuedSongs(viewModel.albumSongInfo)
                        for ( i in viewModel.albumSongInfo) {
                            player.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    }
                    player.prepare()
                    player.seekTo(viewModel.songIterator, 0L)
                    viewModel.incrementSongIterator(1)
                }
                viewModel.updateShuffleMode(!viewModel.shuffleMode)
            },
            modifier = Modifier
                .size(40.dp),
            colors = IconButtonColors(
                containerColor = Color.Transparent,
                contentColor = viewModel.iconColor,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            content = {
                Icon(
                    painter = (
                            if (!viewModel.shuffleMode) {
                                painterResource(R.drawable.arrow_right)
                            } else {
                                painterResource(R.drawable.shuffle)
                            }
                            ),
                    contentDescription = "Shuffle controls"
                )
            }
        )
        Spacer(modifier = Modifier.width(15.dp))
    }
}
@OptIn(UnstableApi::class)
@Composable
fun GraphicalEqualizer(spectrumAnalyzer: SpectrumAnalyzer, viewModel: PlayerViewModel) {
    Row(
        modifier = Modifier
            .size(320.dp, 140.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val fieldName = listOf("63","160","400","1k","2.5k","6.3k","16k")
        VolumeLevelAxis(viewModel)
        Column( // Volume level
            modifier = Modifier
                .fillMaxHeight()
                .width(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy((-2).dp)
            ) {
                val tick = viewModel.currentSongPosition
                if (viewModel.isPlaying) {
                    VolumeLevelText(spectrumAnalyzer, tick)
                    VolumeLevelText(spectrumAnalyzer, tick)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .width(15.dp)
        )
        EQLevelAxis()
        for (i in 0..6) { // 7 band EQ
            AudioLevelColumn(fieldName[i],spectrumAnalyzer, viewModel)
        }
    }
}

@Composable
fun VolumeLevelAxis(viewModel: PlayerViewModel) {
    Column( // Arbitrary measure dashes
        modifier = Modifier
            .fillMaxHeight()
            .width(10.dp)
            .offset(y = 9.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 1.dp),
            text = "20",
            fontWeight = FontWeight.W300,
            fontSize = 5.sp,
            color = viewModel.eqTextColor,
            lineHeight = 2.sp,
        )
        for (i in 1..4) {
            VolumeLevelTick(viewModel)
        }
        Text(
            modifier = Modifier
                .padding(horizontal = 1.dp),
            text = "10",
            fontWeight = FontWeight.W300,
            fontSize = 5.sp,
            color = viewModel.eqTextColor,
            lineHeight = 2.sp,
        )
        for (i in 1..4) {
            VolumeLevelTick(viewModel)
        }
        Text(
            modifier = Modifier
                .padding(horizontal = 1.dp),
            text = "0",
            fontWeight = FontWeight.W300,
            fontSize = 5.sp,
            color = viewModel.eqTextColor,
            lineHeight = 10.sp,
        )
    }
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .offset(y = 7.dp)
    ) {
        drawRect(
            color = viewModel.eqTextColor,
            size = Size(width = 1f, height = 140.dp.toPx()),
        )
    }
}
@Composable
fun EQLevelAxis(viewModel: PlayerViewModel) {
    Column( // Arbitrary measure dashes
        modifier = Modifier
            .fillMaxHeight()
            .width(10.dp)
            .offset(y = 9.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..8) {
            VolumeLevelTick(viewModel)
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .offset(y = 7.dp)
    ) {
        drawRect(
            color = viewModel.eqTextColor,
            size = Size(width = 1f, height = 140.dp.toPx()),
        )
    }
}
@Composable
fun VolumeLevelTick(viewModel: PlayerViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy((-2).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = "_",
                fontWeight = FontWeight.W300,
                fontSize = 7.sp,
                color = viewModel.eqTextColor,
                lineHeight = 10.sp,
            )
            Text(
                modifier = Modifier,
                text = "_",
                fontWeight = FontWeight.W300,
                fontSize = 7.sp,
                color = viewModel.eqTextColor,
                lineHeight = 10.sp,
            )
            Text(
                modifier = Modifier,
                text = "_",
                fontWeight = FontWeight.W300,
                fontSize = 7.sp,
                color = viewModel.eqTextColor,
                lineHeight = 10.sp,
            )
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
            Modifier,
            viewModel
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy((-2).dp)
        ) {
            val tick = viewModel.currentSongPosition
            if (viewModel.isPlaying) {
                AudioLevelText(fieldName, spectrumAnalyzer, tick, viewModel)
                AudioLevelText(fieldName, spectrumAnalyzer, tick, viewModel)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun AudioLevelText(fieldName: String, spectrumAnalyzer: SpectrumAnalyzer, tick: Float, viewModel: PlayerViewModel) {
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
        color = viewModel.eqLevelColor,
        letterSpacing = 0.sp,
        lineHeight = 3.sp,
        textAlign = TextAlign.Center
        )
}
@OptIn(UnstableApi::class)
@Composable
fun VolumeLevelText(spectrumAnalyzer: SpectrumAnalyzer, tick: Float) {
    val eqTransition = rememberInfiniteTransition()
    val target = remember(tick) {
        volumeLevel(spectrumAnalyzer)
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
fun volumeLevel(spectrumAnalyzer: SpectrumAnalyzer): Float {
    var tmpSound = spectrumAnalyzer.volume
    if (tmpSound > 20000.0) {
        tmpSound = 20000.0
    }
    return when {
        tmpSound <= 2244 -> 2f // 1000
        tmpSound <= 2512 -> 4f // 2000 etc.
        tmpSound <= 2818 -> 6f
        tmpSound <= 3162 -> 8f
        tmpSound <= 3548 -> 10f
        tmpSound <= 3981 -> 12f
        tmpSound <= 4467 -> 14f
        tmpSound <= 5012 -> 16f
        tmpSound <= 5623 -> 18f
        tmpSound <= 6325 -> 20f
        tmpSound <= 7096 -> 22f
        tmpSound <= 7943 -> 24f
        tmpSound <= 8913 -> 26f
        tmpSound <= 10000 -> 28f
        tmpSound <= 11220 -> 30f
        tmpSound <= 12589 -> 32f
        tmpSound <= 14125 -> 34f
        tmpSound <= 15849 -> 36f
        tmpSound <= 17783 -> 38f
        tmpSound <= 20000 -> 40f
        else -> 0f
    }
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
fun DotoText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = text,
        fontFamily = dotoFamily,
        fontWeight = FontWeight.W300,
        fontSize = 8.sp,
        color = viewModel.eqTextColor,
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
            .height(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var currentSongPosition by remember { mutableFloatStateOf(viewModel.currentSongPosition) }
        LcdText(
            "${(viewModel.currentSongPosition / 60).toInt()}:${(viewModel.currentSongPosition % 60).toInt()}",
            viewModel = viewModel
        )
        Slider(
            value = viewModel.currentSongPosition,
            valueRange = 0f..viewModel.duration,
            modifier = Modifier
                .size(250.dp, 20.dp),
            onValueChange = {
                currentSongPosition = it
                viewModel.updateSongPosition(player, currentSongPosition.toLong())
            },
            thumb = {
                SliderThumb(viewModel)
            },
            track = {
                SliderTrack(viewModel)
            },
        )
        LcdText(
            "${(viewModel.duration / 60).toInt()}:${(viewModel.duration % 60).toInt()}",
            viewModel = viewModel
        )
    }
}
@Composable
fun SliderThumb(viewModel: PlayerViewModel) {
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
                    color = viewModel.sliderThumbColor,
                    radius = 25f,
                    center = this.center,
                    style = Fill
                )
            }
        )
    }
}
@Composable
fun SliderTrack(viewModel: PlayerViewModel) {
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
                    color = viewModel.sliderTrackColor,
                    cornerRadius = CornerRadius(10f,10f),
                    topLeft = Offset(0f,-6.5f)
                )
            }
        )
    }
}