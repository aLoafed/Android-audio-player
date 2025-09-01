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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController

@Composable
fun AlbumSongsScreen(album: String, songInfo: List<SongInfo>, player: ExoPlayer, viewModel: PlayerViewModel, navController: NavController) {
    val albumSongsList = mutableListOf<SongInfo>()
    for (i in 0 until songInfo.count()) {
        if (songInfo[i].album == album) {
            albumSongsList.add(songInfo[i])
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentPadding = PaddingValues(bottom = 55.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        items(albumSongsList.count()) { i ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(5.dp)
                    .clickable(
                        onClick = {
                            val songUri = albumSongsList[i].songUri
                            val mediaItem = MediaItem.fromUri(songUri)
                            player.setMediaItem(mediaItem)
                            val length = albumSongsList.count() - 1
                            for (j in i + 1..length) {
                                val songUri = albumSongsList[j].songUri
                                val mediaItem = MediaItem.fromUri(songUri)
                                player.addMediaItem(mediaItem)
                            }
                            player.prepare()
                            player.play()
                            viewModel.updateAlbumArt(albumSongsList[i].albumArt)
                            viewModel.updateIsPlaying(true)
                            viewModel.updateSongDuration((albumSongsList[i].time).toLong())
                            viewModel.updateSongInfoIterator(i)
                            viewModel.updatePlayingFromSongsScreen(false) // Shows details from albums list
                            navController.navigate("pager")
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image( // Album art
                    bitmap = albumSongsList[i].albumArt,
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
                        text = albumSongsList[i].name
                    )
                    Spacer(
                        modifier = Modifier
                            .height(5.dp)
                    )
                    LcdText( // Artist name
                        text = albumSongsList[i].artist
                    )
                    LcdText( // Album name
                        text = albumSongsList[i].album
                    )
                }
            }
        }
    }
}