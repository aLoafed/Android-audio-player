package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController

class PlayerLoadingScreen() {
    @OptIn(UnstableApi::class)
    @Composable
    fun LoadingPlayerScreen(viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
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
            LoadingPlayingMediaInfo(viewModel, songInfo)
            PlaybackControls(mediaController, viewModel, songInfo)
            GraphicalEqualizer(spectrumAnalyzer, viewModel)
            RepeatShuffleControls(viewModel, mediaController, songInfo)
            SeekBar(mediaController, viewModel)
        }
    }

    @Composable
    fun LoadingPlayingMediaInfo(viewModel: PlayerViewModel, songInfo: List<SongInfo>) {
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

    @kotlin.OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoadingSeekBar(mediaController: MediaController?, viewModel: PlayerViewModel) {
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
                    viewModel.updateSongPosition(mediaController, currentSongPosition.toLong())
                },
                thumb = {
                    LoadingSliderThumb(viewModel)
                },
                track = {
                    LoadingSliderTrack(viewModel)
                },
            )
            LcdText(
                "${(viewModel.duration / 60).toInt()}:${(viewModel.duration % 60).toInt()}",
                viewModel = viewModel
            )
        }
    }

    @Composable
    fun LoadingSliderThumb(viewModel: PlayerViewModel) {
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
                        color = viewModel.sliderThumbColor.copy(),
                        radius = 25f,
                        center = this.center,
                        style = Fill
                    )
                }
            )
        }
    }

    @Composable
    fun LoadingSliderTrack(viewModel: PlayerViewModel) {
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
}