package com.example.audio_player

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import com.example.audio_player.ui.theme.dotoFamily
import com.example.audio_player.ui.theme.orbitronFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

var shuffleSongInfo = listOf<SongInfo>()

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    mediaController: MediaController?,
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer,
    viewModel: PlayerViewModel,
    songInfo: List<SongInfo>
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        PortraitOrientation(mediaController,spectrumAnalyzer,viewModel,songInfo)
    } else {
        HorizontalOrientation(mediaController,spectrumAnalyzer,viewModel,songInfo)
    }
}
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PortraitOrientation(
    mediaController: MediaController?,
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer,
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
        PlayingMediaInfo(viewModel, songInfo)
        PlaybackControls(mediaController, viewModel)
        if (viewModel.showEqualiser) {
            SpectrumAnalyzer(spectrumAnalyzer, viewModel)
        }
        OtherMediaControls(
            viewModel,
            mediaController,
            songInfo,
            spectrumAnalyzer
        ) // Repeat, shuffle, speed & pitch change
        SeekBar(mediaController, viewModel)
    }
}
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun HorizontalOrientation(
    mediaController: MediaController?,
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer,
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
        AlbumArtHorizontalOrientation(viewModel, songInfo)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPlayingMediaInfo(viewModel, songInfo)
            PlaybackControls(mediaController, viewModel, 46.dp)
            SeekBarAndOtherControls(viewModel, mediaController, songInfo, spectrumAnalyzer)
            if (viewModel.showEqualiser) {
                SpectrumAnalyzer(spectrumAnalyzer, viewModel)
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
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer
) {
    val tmpSongInfo = mutableListOf<SongInfo>()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeekBar(mediaController,viewModel, 0.65f)
        //===================== Other controls =====================//
        // Audio effects
        AudioEffectMenu(viewModel, spectrumAnalyzer, mediaController)
        IconButton( // Repeat controls
            onClick = {
                when (viewModel.repeatMode) {
                    "normal" -> {
                        mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                        viewModel.updateRepeatMode("repeatQueue")
                    }

                    "repeatQueue" -> {
                        mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                        viewModel.updateRepeatMode("repeatSong")
                    }

                    "repeatSong" -> {
                        mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
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
                        mediaController?.clearMediaItems()
                        for (i in tmpShuffledAlbumSongInfo) {
                            tmpSongInfo.add(i)
                        }
                        shuffleSongInfo = tmpSongInfo
                        for (i in shuffleSongInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    } else { // Playing from songs screen
                        val tmpShuffledSongInfo = songInfo.shuffled()
                        tmpSongInfo.clear()
                        mediaController?.clearMediaItems()
                        for (i in tmpShuffledSongInfo) {
                            tmpSongInfo.add(i)
                        }
                        shuffleSongInfo = tmpSongInfo
                        for (i in shuffleSongInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    }
                    viewModel.updateQueuedSongs(shuffleSongInfo)
                    viewModel.updateLastPlayedUnshuffledSong()
                    viewModel.updateSongIterator(0)
                    mediaController?.prepare()
                    mediaController?.play()
                } else { // Switching to normal playback
                    viewModel.updateSongIterator(viewModel.lastPlayedUnshuffledSong)
                    mediaController?.clearMediaItems()
                    if (viewModel.playingFromSongsScreen) { // Playing from songs screen
                        viewModel.updateQueuedSongs(songInfo)
                        for (i in songInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    } else { // Playing from albums screen
                        viewModel.updateQueuedSongs(viewModel.albumSongInfo)
                        for (i in viewModel.albumSongInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    }
                    mediaController?.prepare()
                    mediaController?.seekTo(viewModel.songIterator, 0L)
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

@Composable
fun AlbumArtHorizontalOrientation(viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
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
            .size(250.dp),
        contentDescription = null,
        contentScale = ContentScale.Fit
    )
}
@Composable
fun HorizontalPlayingMediaInfo(viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
    PlayerLargeLcdText(
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
    PlayerLcdText(
        if (viewModel.shuffleMode) {
            shuffleSongInfo[viewModel.songIterator].artist
        } else if (viewModel.playingFromSongsScreen) {
            songInfo[viewModel.songIterator].artist
        } else {
            viewModel.albumSongInfo[viewModel.songIterator].artist
        },
        viewModel = viewModel
    )
    PlayerLcdText(
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
        contentDescription = null,
        contentScale = ContentScale.Fit
    )
    Spacer(
        modifier = Modifier
            .height(10.dp)
    )
    PlayerLargeLcdText( // Was using Extra large
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
    PlayerLcdText( // Was using Large
        if (viewModel.shuffleMode) {
            shuffleSongInfo[viewModel.songIterator].artist
        } else if (viewModel.playingFromSongsScreen) {
            songInfo[viewModel.songIterator].artist
        } else {
            viewModel.albumSongInfo[viewModel.songIterator].artist
        },
        viewModel = viewModel
    )
    PlayerLcdText(
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
        IconButton(
            // Previous button
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
                    } catch (e: IndexOutOfBoundsException) {
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
}

@OptIn(UnstableApi::class)
@ExperimentalMaterial3Api
@Composable
fun OtherMediaControls(
    viewModel: PlayerViewModel,
    mediaController: MediaController?,
    songInfo: List<SongInfo>,
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer
) {
    val tmpSongInfo = mutableListOf<SongInfo>()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Audio effects
        AudioEffectMenu(viewModel, spectrumAnalyzer, mediaController)
        IconButton( // Repeat controls
            onClick = {
                when (viewModel.repeatMode) {
                    "normal" -> {
                        mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                        viewModel.updateRepeatMode("repeatQueue")
                    }

                    "repeatQueue" -> {
                        mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                        viewModel.updateRepeatMode("repeatSong")
                    }

                    "repeatSong" -> {
                        mediaController?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
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
                        mediaController?.clearMediaItems()
                        for (i in tmpShuffledAlbumSongInfo) {
                            tmpSongInfo.add(i)
                        }
                        shuffleSongInfo = tmpSongInfo
                        for (i in shuffleSongInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    } else { // Playing from songs screen
                        val tmpShuffledSongInfo = songInfo.shuffled()
                        tmpSongInfo.clear()
                        mediaController?.clearMediaItems()
                        for (i in tmpShuffledSongInfo) {
                            tmpSongInfo.add(i)
                        }
                        shuffleSongInfo = tmpSongInfo
                        for (i in shuffleSongInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    }
                    viewModel.updateQueuedSongs(shuffleSongInfo)
                    viewModel.updateLastPlayedUnshuffledSong()
                    viewModel.updateSongIterator(0)
                    mediaController?.prepare()
                    mediaController?.play()
                } else { // Switching to normal playback
                    viewModel.updateSongIterator(viewModel.lastPlayedUnshuffledSong)
                    mediaController?.clearMediaItems()
                    if (viewModel.playingFromSongsScreen) { // Playing from songs screen
                        viewModel.updateQueuedSongs(songInfo)
                        for (i in songInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    } else { // Playing from albums screen
                        viewModel.updateQueuedSongs(viewModel.albumSongInfo)
                        for (i in viewModel.albumSongInfo) {
                            mediaController?.addMediaItem(MediaItem.fromUri(i.songUri))
                        }
                    }
                    mediaController?.prepare()
                    mediaController?.seekTo(viewModel.songIterator, 0L)
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
fun SpectrumAnalyzer(
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer,
    viewModel: PlayerViewModel
) {
    val scope = rememberCoroutineScope()
    var eqList by remember { mutableStateOf(doubleArrayOf()) }
    var volume by remember { mutableDoubleStateOf(0.0) }

    // Collecting flow data from spectrum analyzer
    val stateFlowData = spectrumAnalyzer.eqStateFlow.collectAsState()
    remember(stateFlowData.value) {
        scope.launch {
            delay(1850)
            eqList = stateFlowData.value.eqList
            volume = stateFlowData.value.volume
        }
    }
    Row(
        modifier = Modifier
            .size(340.dp, 140.dp)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val fieldName = listOf("63", "16O", "4OO", "1k", "2.5k", "6.3k", "16k")
        VolumeLevelAxis(viewModel)
        //======================== Volume level ========================//
        Column(
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
                    VolumeLevelText(tick, viewModel, volume)
                    VolumeLevelText(tick, viewModel, volume)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .width(15.dp)
        )
        //======================== Equaliser ========================//
        EQLevelAxis(viewModel)
        for (i in 0..6) { // 7 band EQ
            EQLevelColumn(fieldName[i], viewModel, eqList)
        }
    }
}

@Composable
fun VolumeLevelAxis(viewModel: PlayerViewModel) {
    val colorAlpha = 0.65f
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(11.5.dp)
            .offset(y = 9.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 1.dp),
            text = "2O",
            fontWeight = FontWeight.W300,
            fontFamily = orbitronFamily,
            fontSize = 5.sp,
            color = viewModel.eqTextColor,
            lineHeight = 2.sp,
            style = TextStyle(
                shadow = Shadow(
                    color = viewModel.eqTextColor.copy(alpha = colorAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
        for (i in 1..4) {
            VolumeLevelTick(viewModel)
        }
        Text(
            modifier = Modifier
                .offset(y = 2.dp)
                .padding(horizontal = 1.dp),
            text = "1O",
            fontWeight = FontWeight.W300,
            fontFamily = orbitronFamily,
            fontSize = 5.sp,
            color = viewModel.eqTextColor,
            lineHeight = 2.sp,
            style = TextStyle(
                shadow = Shadow(
                    color = viewModel.eqTextColor.copy(alpha = 0.6f),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
        for (i in 1..4) {
            VolumeLevelTick(viewModel)
        }
        Text(
            modifier = Modifier
                .offset(y = 2.dp)
                .padding(horizontal = 1.dp),
            text = "O",
            fontFamily = orbitronFamily,
            fontWeight = FontWeight.W300,
            fontSize = 5.sp,
            color = viewModel.eqTextColor,
            lineHeight = 10.sp,
            style = TextStyle(
                shadow = Shadow(
                    color = viewModel.eqTextColor.copy(alpha = colorAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            )
        )
    }
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .offset(y = 15.dp)
            .shadow(
                shape = RectangleShape,
                elevation = 2.dp,
                ambientColor = viewModel.eqTextColor.copy(alpha = 0.8f)
            )
    ) {
        drawRect(
            color = viewModel.eqTextColor,
            size = Size(width = 1f, height = 131.dp.toPx()),
        )
    }
}

@Composable
fun EQLevelAxis(viewModel: PlayerViewModel) {
    Column( // Arbitrary measure dashes
        modifier = Modifier
            .fillMaxHeight()
            .width(7.dp)
            .offset(y = 9.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..8) {
            VolumeLevelTick(viewModel)
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .offset(y = 16.dp)
            .shadow(
                shape = RectangleShape,
                elevation = 2.dp,
                ambientColor = viewModel.eqTextColor.copy(alpha = 0.8f)
            )
    ) {
        drawRect(
            color = viewModel.eqTextColor,
            size = Size(width = 1f, height = 130.65.dp.toPx()),
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
            for (i in 1..3) {
                Text(
                    modifier = Modifier,
                    text = "_",
                    fontWeight = FontWeight.W300,
                    fontSize = 7.sp,
                    color = viewModel.eqTextColor,
                    lineHeight = 10.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = viewModel.eqTextColor.copy(alpha = 0.8f),
                            offset = Offset(0f, 0f),
                            blurRadius = 8f
                        )
                    )
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun EQLevelColumn(fieldName: String, viewModel: PlayerViewModel, eqList: DoubleArray) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(35.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OrbitronText(
            fieldName,
            Modifier,
            viewModel
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy((-2).dp)
        ) {
            val tick = viewModel.currentSongPosition
            if (viewModel.isPlaying) {
                EQLevelText(fieldName, tick, viewModel, eqList)
                EQLevelText(fieldName, tick, viewModel, eqList)
            }
        }
    }
}

//============================== EQ level ==============================//
@OptIn(UnstableApi::class)
@Composable
fun EQLevelText(fieldName: String, tick: Float, viewModel: PlayerViewModel, eqList: DoubleArray) {
    val eqTransition = rememberInfiniteTransition()

    val target = remember(tick) {
        if (eqList.count() != 0) {
            when (fieldName) {
                "63" -> level63(eqList[0])
                "16O" -> level160(eqList[1])
                "4OO" -> level400(eqList[2])
                "1k" -> level1k(eqList[3])
                "2.5k" -> level2500k(eqList[4])
                "6.3k" -> level6300k(eqList[5])
                "16k" -> level16k(eqList[6])
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
        textAlign = TextAlign.Center,
    )
}

//============================== Volume level ==============================//
@OptIn(UnstableApi::class)
@Composable
fun VolumeLevelText(
    tick: Float,
    viewModel: PlayerViewModel,
    volumeLevel: Double
) {
    val eqTransition = rememberInfiniteTransition()
    val target = remember(tick) {
        volumeLevel(volumeLevel)
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
fun volumeLevel(volume: Double): Float {
    var tmpSound = volume

    if (tmpSound > 20000.0) {
        tmpSound = 20000.0
    }
    return when {
        tmpSound <= 2244 -> 2f
        tmpSound <= 2512 -> 4f
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
fun level63(data: Double): Float {
//    var tempValue = spectrumAnalyzer.eqList[0]
    var tempValue = data
    if (tempValue > 110.0) {
        tempValue = 110.0
    }
    tempValue = tempValue / 5.5 * 2
    return tempValue.toFloat()
}

@OptIn(UnstableApi::class)
fun level160(magnitude: Double): Float {
    var tempValue = magnitude
    if (tempValue > 55.0) {
        tempValue = 55.0
    }
    tempValue = tempValue / 2.75 * 2
    return tempValue.toFloat()
}

@OptIn(UnstableApi::class)
fun level400(magnitude: Double): Float {
    var tempValue = magnitude
    if (tempValue > 40.0) {
        tempValue = 40.0
    }
    tempValue = tempValue / 2.0 * 2
    return tempValue.toFloat()
}

@OptIn(UnstableApi::class)
fun level1k(magnitude: Double): Float {
    var tempValue = magnitude
    if (tempValue > 13.0) {
        tempValue = 13.0
    }
    tempValue = tempValue / 0.65 * 2
    return tempValue.toFloat()
}

@OptIn(UnstableApi::class)
fun level2500k(magnitude: Double): Float {
    var tempValue = magnitude
    if (tempValue > 5.0) {
        tempValue = 5.0
    }
    tempValue = tempValue / 0.25 * 2
    return tempValue.toFloat()
}

@OptIn(UnstableApi::class)
fun level6300k(magnitude: Double): Float {
    var tempValue = magnitude
    if (tempValue > 1.5) {
        tempValue = 1.5
    }
    tempValue = tempValue / 0.075 * 2
    return tempValue.toFloat()
}

@OptIn(UnstableApi::class)
fun level16k(magnitude: Double): Float {
    var tempValue = magnitude
    if (tempValue > 1.5) {
        tempValue = 1.5
    }
    tempValue = tempValue / 0.075 * 2
    return tempValue.toFloat()
}

//============================== Text presets ==============================//
@Composable
fun OrbitronText(text: String, modifier: Modifier = Modifier, viewModel: PlayerViewModel) {
    Text(
        modifier = modifier,
        text = text,
        fontFamily = orbitronFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 8.sp,
        color = viewModel.eqTextColor,
        style = TextStyle(
            shadow = Shadow(
                color = viewModel.eqTextColor.copy(alpha = 0.8f),
                offset = Offset(0f, 0f),
                blurRadius = 20f
            )
        )
    )
}

fun textLevelBuilder(n: IntRange): String {
    var tempText = ""
    for (i in n) {
        tempText += "_\n"
    }
    return tempText
}

//============================== Seek bar ==============================//
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(mediaController: MediaController?, viewModel: PlayerViewModel, widthFactor: Float = 1f) {
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
    spectrumAnalyzer: PlayerService.SpectrumAnalyzer,
    mediaController: MediaController?,
) {
    var expanded by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(spectrumAnalyzer.speed) }
    var pitch by remember { mutableFloatStateOf(spectrumAnalyzer.pitch) }
    val popupOffset = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        IntOffset(175, -345)
    } else {
        IntOffset(60, 330)
    }

    IconButton( // Speed & pitch change
        onClick = {
            if (!expanded) {
                expanded = true
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
        enabled = !expanded,
        content = {
            Icon(
                painter = (
                        painterResource(R.drawable.speed_pitch)
                        ),
                contentDescription = "Audio effects"
            )
        }
    )
    if (expanded) {
        Popup(
            alignment = Alignment.Center,
            offset = popupOffset,
            onDismissRequest = {
                expanded = false
                speed = spectrumAnalyzer.speed
                pitch = spectrumAnalyzer.pitch
            },
        ) {
            Column(
                modifier = Modifier
                    .size(150.dp, 215.dp)
                    .background(viewModel.backgroundColor)
                    .border(0.dp, viewModel.iconColor),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .size(140.dp, 180.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    val sliderHeight = 120.dp
                    // Speed controls
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(70.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LcdText(
                            "Speed:",
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
                            value = speed,
                            valueRange = 0f..2f,
                            steps = 39, // For 0.05 increments
                            onValueChange = {
                                speed = it
                            },
                            thumb = {
                                SliderThumb(viewModel)
                            },
                            track = {
                                SettingsSliderTrack(viewModel)
                            },
                        )
                        LargeLcdText(
                            "%.2f".format(speed),
                            viewModel = viewModel,
                            lineHeight = 1.sp
                        )
                    }

                    // Pitch controls
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(70.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LcdText(
                            "Pitch:",
                            viewModel = viewModel
                        )
                        Slider(
                            value = pitch,
                            valueRange = 0f..2f,
                            steps = 39, // For 0.05 increments
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
                                pitch = it
                            },
                            thumb = {
                                SliderThumb(viewModel)
                            },
                            track = {
                                SettingsSliderTrack(viewModel)
                            },
                        )
                        LargeLcdText(
                            "%.2f".format(pitch),
                            viewModel = viewModel
                        )
                    }
                }
                // Apply changes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(50.dp),
                        onClick = {
                            mediaController?.pause()
                            spectrumAnalyzer.speed = speed
                            spectrumAnalyzer.pitch = pitch
                            if (spectrumAnalyzer.pitch != 1f || spectrumAnalyzer.speed != 1f) {
                                spectrumAnalyzer.usingSonicProcessor = true
                            } else {
                                false
                            }
                            spectrumAnalyzer.configure(
                                AudioProcessor.AudioFormat(
                                    44100,
                                    2,
                                    C.ENCODING_PCM_16BIT
                                )
                            )
                            spectrumAnalyzer.flush()
                            mediaController?.play()
                            expanded = false
                        },
                        content = {
                            Icon(
                                painterResource(R.drawable.done_tick),
                                contentDescription = "Apply changes"
                            )
                        },
                        colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = viewModel.iconColor,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                    )
                }
            }
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