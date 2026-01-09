package com.example.audio_player

import android.content.res.Configuration
import android.media.audiofx.PresetReverb
import androidx.annotation.OptIn
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import java.lang.Thread.sleep

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    mediaController: MediaController?,
    audioProcessor: PlayerService.SpectrumAnalyzer,
    viewModel: PlayerViewModel,
    songInfo: List<SongInfo>
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        PortraitOrientation(mediaController, audioProcessor, viewModel, songInfo)
    } else {
        HorizontalOrientation(mediaController, audioProcessor, viewModel, songInfo)
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PortraitOrientation(
    mediaController: MediaController?,
    audioProcessor: PlayerService.SpectrumAnalyzer,
    viewModel: PlayerViewModel,
    songInfo: List<SongInfo>
) {
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
        PlayingMediaInfo(viewModel)
        PlaybackControls(mediaController, viewModel)
        if (viewModel.showEqualiser) {
            SpectrumAnalyzer(audioProcessor, viewModel)
        }
        OtherMediaControls(
            viewModel,
            mediaController,
            songInfo,
            audioProcessor
        )
        SeekBar(mediaController, viewModel)
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun HorizontalOrientation(
    mediaController: MediaController?,
    audioProcessor: PlayerService.SpectrumAnalyzer,
    viewModel: PlayerViewModel,
    songInfo: List<SongInfo>
) {
    Spacer(Modifier.width(10.dp))
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.displayCutout),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumArtHorizontalOrientation(viewModel)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPlayingMediaInfo(viewModel)
            PlaybackControls(mediaController, viewModel, 46.dp)
            SeekBarAndOtherControls(viewModel, mediaController, songInfo, audioProcessor)
            if (viewModel.showEqualiser) {
                SpectrumAnalyzer(audioProcessor, viewModel)
            }
        }
    }
}

@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun SeekBarAndOtherControls(
    viewModel: PlayerViewModel,
    mediaController: MediaController?,
    songInfo: List<SongInfo>,
    audioProcessor: PlayerService.SpectrumAnalyzer
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeekBar(mediaController, viewModel, 0.65f)
        //===================== Other controls =====================//
        // Audio effects
        AudioEffectMenu(viewModel, audioProcessor, mediaController)
        RepeatControls(mediaController, viewModel)
        ShuffleControls(mediaController, viewModel, songInfo)
        Spacer(modifier = Modifier.width(15.dp))
    }
}

@Composable
fun AlbumArtHorizontalOrientation(viewModel: PlayerViewModel) {
    Image( // Album art
        bitmap = (
                try {
                    viewModel.queuedSongs[viewModel.songIndex].albumArt
                } catch (_: IndexOutOfBoundsException) {
                    ImageBitmap(500, 500)
                }
                ),
        modifier = Modifier
            .size(250.dp),
        contentDescription = null,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun HorizontalPlayingMediaInfo(viewModel: PlayerViewModel) {
    // Song name
    PlayerLargeLcdText(
        text = (
                try {
                    viewModel.queuedSongs[viewModel.songIndex].name
                } catch (_: IndexOutOfBoundsException) {
                    ""
                }
                ),
        viewModel = viewModel
    )
    Spacer(
        modifier = Modifier
            .height(5.dp)
    )
    // Artist name
    PlayerLcdText(
        try {
            viewModel.queuedSongs[viewModel.songIndex].artist
        } catch (_: IndexOutOfBoundsException) {
            ""
        },
        viewModel = viewModel
    )
    // Album name
    PlayerLcdText(
        try {
            viewModel.queuedSongs[viewModel.songIndex].album
        } catch (_: IndexOutOfBoundsException) {
            ""
        },
        viewModel = viewModel
    )
}

@Composable
fun PlayingMediaInfo(viewModel: PlayerViewModel) {
    // Album art
    Image(
        bitmap = (
                try {
                    viewModel.queuedSongs[viewModel.songIndex].albumArt
                } catch (_: IndexOutOfBoundsException) {
                    ImageBitmap(500, 500)
                }
                ),
        modifier = Modifier
            .size(300.dp),
        contentDescription = null,
        contentScale = ContentScale.Fit
    )
    Spacer(
        modifier = Modifier.height(10.dp)
    )
    // Song name
    PlayerLargeLcdText( // Was using Extra large
        text = (
                try {
                    viewModel.queuedSongs[viewModel.songIndex].name
                } catch (_: IndexOutOfBoundsException) {
                    ""
                }
                ),
        viewModel = viewModel
    )
    Spacer(
        modifier = Modifier
            .height(5.dp)
    )
    // Artist name
    PlayerLcdText( // Was using Large
        text = (
                try {
                    viewModel.queuedSongs[viewModel.songIndex].artist
                } catch (_: IndexOutOfBoundsException) {
                    ""
                }
                ),
        viewModel = viewModel
    )
    // Album name
    PlayerLcdText(
        text = (
                try {
                    viewModel.queuedSongs[viewModel.songIndex].album
                } catch (_: IndexOutOfBoundsException) {
                    ""
                }
                ),
        viewModel = viewModel
    )
}

@Composable
fun PlaybackControls(
    mediaController: MediaController?,
    viewModel: PlayerViewModel,
    height: Dp = 60.dp
) {
    Row( // Playback controls
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PreviousButton(mediaController, viewModel)
        PlayPauseButton(mediaController, viewModel)
        SkipButton(mediaController, viewModel)
    }
}

@Composable
fun PreviousButton(mediaController: MediaController?, viewModel: PlayerViewModel) {
    IconButton(
        modifier = Modifier
            .size(60.dp),
        onClick = {
            if (mediaController != null) {
                try {
                    if (mediaController.currentPosition < 10000L) {
                        if (mediaController.hasPreviousMediaItem()) {
                            mediaController.seekToPreviousMediaItem()
                        }
                    } else {
                        mediaController.seekTo(0L)
                    }
                } catch (_: IndexOutOfBoundsException) {
                }
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
}

@Composable
fun SkipButton(mediaController: MediaController?, viewModel: PlayerViewModel) {
    IconButton(
        // Skip button
        modifier = Modifier
            .size(60.dp),
        onClick = {
            if (mediaController != null) {
                if (mediaController.hasNextMediaItem()) {
                    mediaController.seekToNextMediaItem()
                }
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

@Composable
fun PlayPauseButton(mediaController: MediaController?, viewModel: PlayerViewModel) {
    IconButton( // Play & pause button
        onClick = {
            if (mediaController != null) {
                if (mediaController.isPlaying) {
                    mediaController.pause()
                } else {
                    mediaController.play()
                }
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
}

@OptIn(UnstableApi::class)
@ExperimentalMaterial3Api
@Composable
fun OtherMediaControls(
    viewModel: PlayerViewModel,
    mediaController: MediaController?,
    songInfo: List<SongInfo>,
    audioProcessor: PlayerService.SpectrumAnalyzer
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AudioEffectMenu(viewModel, audioProcessor, mediaController)
        RepeatControls(mediaController, viewModel)
        ShuffleControls(mediaController, viewModel, songInfo)
        Spacer(modifier = Modifier.width(15.dp))
    }
}

@Composable
fun RepeatControls(mediaController: MediaController?, viewModel: PlayerViewModel) {
    IconButton( // Repeat controls
        onClick = {
            when (viewModel.repeatMode) {
                "normal" -> {
                    mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                    viewModel.repeatMode = "repeatQueue"
                }

                "repeatQueue" -> {
                    mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                    viewModel.repeatMode = "repeatSong"
                }

                "repeatSong" -> {
                    mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                    viewModel.repeatMode = "normal"
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
                            "normal" -> painterResource(R.drawable.repeat)
                            "repeatQueue" -> painterResource(R.drawable.repeat_on)
                            "repeatSong" -> painterResource(R.drawable.repeat_one_on)
                            else -> painterResource(R.drawable.repeat)
                        }
                        ),
                contentDescription = "Repeat controls"
            )
        }
    )
}

@Composable
fun ShuffleControls(mediaController: MediaController?, viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
    val tmpSongInfo = mutableListOf<SongInfo>()
    IconButton( // Shuffle controls
        onClick = {
            if (!viewModel.shuffleMode) { // Switching to shuffle
                if (!viewModel.playingFromSongsScreen) { // Playing from albums
                    val tmpShuffledAlbumSongInfo = viewModel.albumSongInfo.shuffled()
                    tmpSongInfo.clear()
                    mediaController?.clearMediaItems()
                    for (i in tmpShuffledAlbumSongInfo) {
                        tmpSongInfo.add(i)
                    }
                    viewModel.shuffleSongInfo = tmpSongInfo
                    for (i in viewModel.shuffleSongInfo) {
                        mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                    }
                } else { // Playing from songs screen
                    val tmpShuffledSongInfo = songInfo.shuffled()
                    tmpSongInfo.clear()
                    mediaController?.clearMediaItems()
                    for (i in tmpShuffledSongInfo) {
                        tmpSongInfo.add(i)
                    }
                    viewModel.shuffleSongInfo = tmpSongInfo
                    for (i in viewModel.shuffleSongInfo) {
                        mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                    }
                }
                viewModel.queuedSongs = viewModel.shuffleSongInfo.toMutableStateList()
                viewModel.updateLastPlayedUnshuffledSong()
                viewModel.songIndex = 0
                mediaController?.prepare()
                mediaController?.play()
            } else { // Switching to normal playback
                viewModel.songIndex = viewModel.lastPlayedUnshuffledSong
                mediaController?.clearMediaItems()
                if (viewModel.playingFromSongsScreen) { // Playing from songs screen
                    viewModel.queuedSongs = songInfo.toMutableStateList()
                    for (i in songInfo) {
                        mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                    }
                } else { // Playing from albums screen
                    viewModel.queuedSongs = viewModel.albumSongInfo.toMutableStateList()
                    for (i in viewModel.albumSongInfo) {
                        mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                    }
                }
                mediaController?.prepare()
                mediaController?.seekTo(viewModel.songIndex, 0L)
                viewModel.incrementSongIterator(1)
            }
            viewModel.shuffleMode = !viewModel.shuffleMode
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
}

//============================== Seek bar ==============================//
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(
    mediaController: MediaController?,
    viewModel: PlayerViewModel,
    widthFactor: Float = 1f
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(widthFactor)
            .height(20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var currentSongPosition by remember { mutableFloatStateOf(viewModel.currentSongPosition) }
        var isSeeking by remember { mutableStateOf(false) }
        LcdText(
            getSongPositionString(viewModel, isSeeking, currentSongPosition),
            viewModel = viewModel
        )
        Slider(
            value = currentSongPosition,
            valueRange = 0f..viewModel.duration,
            modifier = Modifier
                .size(244.dp, 20.dp),
            onValueChange = {
                isSeeking = true
                currentSongPosition = it
            },
            onValueChangeFinished = {
                viewModel.updateSongPosition(mediaController, currentSongPosition.toLong())
                sleep(40)
                isSeeking = false
            },
            thumb = {
                SliderThumb(viewModel)
            },
            track = {
                SliderTrack(viewModel)
            },
        )
        LcdText(
            getSongDurationString(viewModel),
            viewModel = viewModel
        )
        LaunchedEffect(viewModel.currentSongPosition) {
            if (!isSeeking) {
                currentSongPosition = viewModel.currentSongPosition
            }
        }
    }
}

fun getSongPositionString(
    viewModel: PlayerViewModel,
    isSeeking: Boolean,
    currentSongPosition: Float
): String {
    var seconds: String
    var minutes: String
    if (!isSeeking) {
        minutes = "${(viewModel.currentSongPosition / 60).toInt()}:"
        seconds = "${(viewModel.currentSongPosition % 60).toInt()}"
    } else {
        minutes = "${(currentSongPosition / 60).toInt()}:"
        seconds = "${(currentSongPosition % 60).toInt()}"
    }
    while (seconds.length < 2) {
        seconds = "0$seconds"
    }
    return "$minutes$seconds"
}

fun getSongDurationString(viewModel: PlayerViewModel): String {
    val minutes = "${(viewModel.duration / 60).toInt()}:"
    var seconds = "${(viewModel.duration % 60).toInt()}"
    while (seconds.length < 2) {
        seconds = "0$seconds"
    }
    return "$minutes$seconds"
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
                    cornerRadius = CornerRadius(10f, 10f),
                    topLeft = Offset(0f, -6.5f)
                )
            }
        )
    }
}

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun AudioEffectMenu(
    viewModel: PlayerViewModel,
    audioProcessor: PlayerService.SpectrumAnalyzer,
    mediaController: MediaController?,
) {
    val speed = TmpAudioEffectValue(remember { mutableFloatStateOf(audioProcessor.speed) })// by remember { mutableFloatStateOf(audioProcessor.speed) }
    val pitch = TmpAudioEffectValue(remember { mutableFloatStateOf(audioProcessor.pitch) })// by remember { mutableFloatStateOf(audioProcessor.pitch) }

    val popupOffset =
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            IntOffset(175, -345)
        } else {
            IntOffset(60, 330)
        }
    IconButton( // Speed & pitch change
        onClick = {
            if (!viewModel.audioEffectMenuExpanded) {
                viewModel.audioEffectMenuExpanded = true
            }
        },
        modifier = Modifier
            .size(40.dp),
        colors = IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = viewModel.iconColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = viewModel.iconColor,
        ),
        enabled = !viewModel.audioEffectMenuExpanded,
    ) {
        Icon(
            painter = (
                    painterResource(R.drawable.speed_pitch)
                    ),
            contentDescription = "Audio effects"
        )
    }
    // Popup audio effects menu
    if (viewModel.audioEffectMenuExpanded) {
        Popup(
            alignment = Alignment.Center,
            offset = popupOffset,
            onDismissRequest = {
                viewModel.audioEffectMenuExpanded = false
                speed.value = mutableFloatStateOf(audioProcessor.speed)
                pitch.value = mutableFloatStateOf(audioProcessor.pitch)
            },
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .size(viewModel.menuWidth, 215.dp)
                    .background(viewModel.backgroundColor)
                    .border(0.dp, viewModel.iconColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val sliderColumnWidth by remember {
                    derivedStateOf {
                        if (viewModel.showReverbValueSlider) {
                            (viewModel.menuWidth.value / 4f).dp
                        } else {
                            (viewModel.menuWidth.value / 3f).dp
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .size(viewModel.menuWidth, 180.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    val sliderHeight = 120.dp // Is read as width due to rotation
                    // Speed slider
                    SpeedPitchSlider(viewModel, sliderColumnWidth, sliderHeight, speed, "Speed")
                    SpeedPitchSlider(viewModel, sliderColumnWidth, sliderHeight, pitch, "Pitch")
                    ReverbTypeSlider(viewModel, sliderColumnWidth, sliderHeight)
                    ReverbValueSlider(viewModel, sliderColumnWidth, sliderHeight)
                }
                ApplyChangesButton(mediaController, audioProcessor, speed, pitch, viewModel)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ApplyChangesButton(
    mediaController: MediaController?,
    audioProcessor: PlayerService.SpectrumAnalyzer,
    speed: TmpAudioEffectValue,
    pitch: TmpAudioEffectValue,
    viewModel: PlayerViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .size(50.dp),
            onClick = {
                val tmpIsPlaying = viewModel.isPlaying
                if (tmpIsPlaying) { mediaController?.pause() }
                audioProcessor.speed = speed.value.floatValue
                audioProcessor.pitch = pitch.value.floatValue
                audioProcessor.setReverbPreset((viewModel.reverbPresetType + viewModel.reverbPresetValue).toShort())
                audioProcessor.usingSonicProcessor = audioProcessor.pitch != 1f || audioProcessor.speed != 1f
                audioProcessor.configure(
                    AudioProcessor.AudioFormat(
                        44100,
                        2,
                        C.ENCODING_PCM_16BIT
                    )
                )
                audioProcessor.flush()
                if (tmpIsPlaying) { mediaController?.play() }
                viewModel.updateSongDuration(
                    audioProcessor.getDurationAfterProcessorApplied(viewModel.duration.toLong())
                )
                viewModel.audioEffectMenuExpanded = false
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = viewModel.iconColor)
        ) {
            Icon(
                painterResource(R.drawable.done_tick),
                contentDescription = "Apply changes"
            )
        }
    }
}

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedPitchSlider(viewModel: PlayerViewModel, sliderColumnWidth: Dp, sliderHeight: Dp, value: TmpAudioEffectValue, type: String) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .animateContentSize()
            .width(sliderColumnWidth),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LcdText(
            "$type:",
            viewModel = viewModel
        )
        Slider(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints.fixed(
                            width = sliderHeight.roundToPx(),
                            height = 15.dp.roundToPx()
                        )
                    )
                    layout(15.dp.roundToPx(), sliderHeight.roundToPx()) {
                        placeable.place(
                            x = -(sliderHeight.roundToPx() - 15.dp.roundToPx()) / 2,
                            y = (sliderHeight.roundToPx() - 15.dp.roundToPx()) / 2,
                        )
                    }
                }
                .graphicsLayer(
                    rotationZ = -90f
                )
                .size(sliderHeight, 15.dp),
            value = value.value.floatValue,
            valueRange = 0f..2f,
            steps = 39, // For 0.05 increments
            onValueChange = {
                value.value.floatValue = it
            },
            thumb = {
                SliderThumb(viewModel)
            },
            track = {
                SettingsSliderTrack(viewModel)
            },
        )
        LargeLcdText(
            "%.2f".format(value.value.floatValue),
            viewModel = viewModel,
            lineHeight = 1.sp
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun ReverbTypeSlider(
    viewModel: PlayerViewModel,
    sliderColumnWidth: Dp,
    sliderHeight: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .animateContentSize()
            .width(sliderColumnWidth),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LcdText(
            "Reverb:",
            viewModel = viewModel
        )
        Slider(
            value = viewModel.reverbPresetType.toFloat(),
            valueRange = 0f..3f,
            steps = 4,
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints.fixed(
                            width = sliderHeight.roundToPx(),
                            height = 15.dp.roundToPx()
                        )
                    )
                    layout(15.dp.roundToPx(), sliderHeight.roundToPx()) {
                        placeable.place(
                            x = -(sliderHeight.roundToPx() - 15.dp.roundToPx()) / 2,
                            y = (sliderHeight.roundToPx() - 15.dp.roundToPx()) / 2,
                        )
                    }
                }
                .graphicsLayer(
                    rotationZ = -90f
                )
                .size(sliderHeight, 15.dp),
            onValueChange = {
                viewModel.reverbPresetType = it.toInt()
            },
            onValueChangeFinished = {
                // Controls the expanded menu
                if (viewModel.reverbPresetType != 3 && viewModel.reverbPresetType != 0) {
                    viewModel.menuWidth = 220.dp
                    viewModel.showReverbValueSlider = true
                    if (viewModel.reverbPresetType == PresetReverb.PRESET_SMALLROOM.toInt()) {
                        viewModel.steps = 3
                        viewModel.reverbValueRange = 0f..2f
                    } else {
                        viewModel.steps = 2
                        viewModel.reverbValueRange = 0f..1f
                    }
                } else {
                    viewModel.menuWidth = 180.dp
                    viewModel.showReverbValueSlider = false
                }
            },
            thumb = {
                SliderThumb(viewModel)
            },
            track = {
                SettingsSliderTrack(viewModel)
            },
        )
        LargeLcdText(
            (
                    when (viewModel.reverbPresetType) {
                        0 -> "None"
                        1 -> "Room"
                        2 -> "Hall"
                        3 -> "Plate"
                        else -> "None"
                    }
                    ),
            viewModel = viewModel
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun ReverbValueSlider(
    viewModel: PlayerViewModel,
    sliderColumnWidth: Dp,
    sliderHeight: Dp
) {
    if (viewModel.showReverbValueSlider) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .animateContentSize()
                .width(sliderColumnWidth),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LcdText(
                "Value:",
                viewModel = viewModel
            )
            val reverbValueSliderHeight = sliderHeight - 15.dp
            Slider(
                value = viewModel.reverbPresetValue.toFloat(),
                valueRange = viewModel.reverbValueRange,
                steps = viewModel.steps,
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints.fixed(
                                width = reverbValueSliderHeight.roundToPx(),
                                height = 15.dp.roundToPx()
                            )
                        )
                        layout(
                            15.dp.roundToPx(),
                            reverbValueSliderHeight.roundToPx()
                        ) {
                            placeable.place(
                                x = -(reverbValueSliderHeight.roundToPx() - 15.dp.roundToPx()) / 2,
                                y = (reverbValueSliderHeight.roundToPx() - 15.dp.roundToPx()) / 2,
                            )
                        }
                    }
                    .graphicsLayer(
                        rotationZ = -90f
                    )
                    .size(sliderColumnWidth - 10.dp, 15.dp),
                onValueChange = {
                    viewModel.reverbPresetValue = it.toInt()
                },
                thumb = {
                    SliderThumb(viewModel)
                },
                track = {
                    SettingsSliderTrack(viewModel, 210f)
                },
            )
            // Text representing the value
            LcdText(
                when (viewModel.steps) {
                    3 -> {
                        when (viewModel.reverbPresetValue) {
                            0 -> "Small"
                            1 -> "Medium"
                            2 -> "Large"
                            else -> "Small"
                        }
                    }

                    2 -> {
                        when (viewModel.reverbPresetValue) {
                            0 -> "Medium"
                            1 -> "Large"
                            else -> "Medium"
                        }
                    }

                    else -> "Error"
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun SettingsSliderTrack(viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier
            .height(15.dp)
            .width(280.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Canvas(
            modifier = Modifier,
            onDraw = {
                drawRoundRect(
                    size = Size(250f, 15f),
                    style = Fill,
                    color = viewModel.sliderTrackColor,
                    cornerRadius = CornerRadius(10f, 10f),
                    topLeft = Offset(0f, -6.5f)
                )
            }
        )
    }
}

@Composable
fun SettingsSliderTrack(viewModel: PlayerViewModel, sliderHeight: Float) {
    Column(
        modifier = Modifier
            .height(15.dp)
            .width((sliderHeight).dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Canvas(
            modifier = Modifier
                .width((sliderHeight).dp), // * 1.12f
            onDraw = {
                drawRoundRect(
                    size = Size(sliderHeight, 15f), // "width" was 250f
                    style = Fill,
                    color = viewModel.sliderTrackColor,
                    cornerRadius = CornerRadius(10f, 10f),
                    topLeft = Offset(0f, -6.5f)
                )
            }
        )
    }
}