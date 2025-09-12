package com.example.audio_player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun SongQueue(viewModel: PlayerViewModel, player: ExoPlayer, songInfo: List<SongInfo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentPadding = PaddingValues(bottom = 55.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        if (viewModel.shuffleMode) {
            items(shuffleSongInfo.count()) { i ->
                for (i in 0 until shuffleSongInfo.count()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(5.dp)
                            .clickable(
                                onClick = {
                                    player.clearMediaItems()
                                    for (j in 0 until shuffleSongInfo.count()) {
                                        player.addMediaItem(MediaItem.fromUri(shuffleSongInfo[j].songUri))
                                    }
                                    player.prepare()
                                    player.seekTo(i,0L)
                                    player.play()
                                    viewModel.updateAlbumArt(shuffleSongInfo[i].albumArt)
                                    viewModel.updateSongDuration((shuffleSongInfo[i].time).toLong())
                                    viewModel.updateSongIterator(i)
                                    viewModel.updatePlayingFromSongsScreen(true)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image( // Album art
                            bitmap = shuffleSongInfo[i].albumArt,
                            modifier = Modifier
                                .size(60.dp),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            LargeLcdText( //Song name
                                text = shuffleSongInfo[i].name,
                                viewModel = viewModel
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(5.dp)
                            )
                            LcdText( // Artist name
                                text = shuffleSongInfo[i].artist,
                                viewModel = viewModel
                            )
                            LcdText( // Album name
                                text = shuffleSongInfo[i].album,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        } else if (viewModel.playingFromSongsScreen) {
            items(songInfo.count()) { i ->
                for (i in 0 until songInfo.count()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(5.dp)
                            .clickable(
                                onClick = {
                                    player.clearMediaItems()
                                    for (j in 0 until songInfo.count()) {
                                        player.addMediaItem(MediaItem.fromUri(songInfo[j].songUri))
                                    }
                                    player.prepare()
                                    player.seekTo(i,0L)
                                    player.play()
                                    viewModel.updateAlbumArt(songInfo[i].albumArt)
                                    viewModel.updateSongDuration((songInfo[i].time).toLong())
                                    viewModel.updateSongIterator(i)
                                    viewModel.updatePlayingFromSongsScreen(true)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image( // Album art
                            bitmap = songInfo[i].albumArt,
                            modifier = Modifier
                                .size(60.dp),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            LargeLcdText( //Song name
                                text = songInfo[i].name,
                                viewModel = viewModel
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(5.dp)
                            )
                            LcdText( // Artist name
                                text = songInfo[i].artist,
                                viewModel = viewModel
                            )
                            LcdText( // Album name
                                text = songInfo[i].album,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        } else {
            items(viewModel.albumSongInfo.count()) { i ->
                for (i in 0 until viewModel.albumSongInfo.count()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(5.dp)
                            .clickable(
                                onClick = {
                                    player.clearMediaItems()
                                    for (j in 0 until viewModel.albumSongInfo.count()) {
                                        player.addMediaItem(MediaItem.fromUri(viewModel.albumSongInfo[j].songUri))
                                    }
                                    player.prepare()
                                    player.seekTo(i,0L)
                                    player.play()
                                    viewModel.updateAlbumArt(viewModel.albumSongInfo[i].albumArt)
                                    viewModel.updateSongDuration((viewModel.albumSongInfo[i].time).toLong())
                                    viewModel.updateSongIterator(i)
                                    viewModel.updatePlayingFromSongsScreen(true)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image( // Album art
                            bitmap = viewModel.albumSongInfo[i].albumArt,
                            modifier = Modifier
                                .size(60.dp),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                        Spacer(
                            modifier = Modifier
                                .width(10.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            LargeLcdText( //Song name
                                text = viewModel.albumSongInfo[i].name,
                                viewModel = viewModel
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(5.dp)
                            )
                            LcdText( // Artist name
                                text = viewModel.albumSongInfo[i].artist,
                                viewModel = viewModel
                            )
                            LcdText( // Album name
                                text = viewModel.albumSongInfo[i].album,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}