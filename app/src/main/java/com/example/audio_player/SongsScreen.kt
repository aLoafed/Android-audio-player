package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.navigation.NavController

@OptIn(UnstableApi::class)
@Composable
fun SongsScreen(
    songInfo: List<SongInfo>,
    mediaController: MediaController?,
    viewModel: PlayerViewModel,
    pagerState: PagerState,
    navController: NavController
) {
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
        items(songInfo.count()) { i ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(5.dp)
                    .clickable(
                        onClick = {
                            mediaController?.clearMediaItems()
                            for (j in 0 until songInfo.count()) {
                                mediaController?.addMediaItem(MediaItem.fromUri(songInfo[j].songUri))
                            }
                            mediaController?.prepare()
                            mediaController?.seekTo(i,0L)
                            mediaController?.play()
                            viewModel.updateQueuedSongs(songInfo)
                            viewModel.updateAlbumArt(songInfo[i].albumArt)
                            viewModel.updateSongDuration((songInfo[i].time).toLong())
                            viewModel.updateSongIterator(i)
                            viewModel.updatePlayingFromSongsScreen(true)
                            pagerState.requestScrollToPage(1)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image( // Album art
                    bitmap = songInfo[i].albumArt,
                    modifier = Modifier
                        .size(65.dp),
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
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.White),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(50.dp),
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.more_menu),
                                contentDescription = "More options",
                                tint = viewModel.iconColor
                            )
                        },
                        onClick = {
                            viewModel.updateSelectedOptionsSong(songInfo[i])
                            navController.navigate("song_options")
                        }
                    )
                }
            }
        }
    }
}