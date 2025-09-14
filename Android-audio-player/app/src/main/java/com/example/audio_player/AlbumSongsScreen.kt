package com.example.audio_player

import android.view.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSongsScreen(album: String, songInfo: List<SongInfo>, player: ExoPlayer, viewModel: PlayerViewModel, navController: NavController) {
    val albumSongsList = mutableListOf<SongInfo>()
    for (i in 0 until songInfo.count()) {
        if (songInfo[i].album == album) {
            albumSongsList.add(songInfo[i])
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(viewModel.backgroundColor),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                colors = IconButtonColors(
                    contentColor = viewModel.iconColor,
                    containerColor = Color.Transparent,
                    disabledContentColor = viewModel.iconColor,
                    disabledContainerColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            LargeLcdText(album, viewModel = viewModel)
        }
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
            items(albumSongsList.count()) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .padding(5.dp)
                        .clickable(
                            onClick = {
                                player.clearMediaItems()
                                viewModel.updateAlbumSongInfo(albumSongsList)
                                for (j in 0 until albumSongsList.count()) {
                                    player.addMediaItem(MediaItem.fromUri(albumSongsList[j].songUri))
                                }
                                player.prepare()
                                player.seekTo(i, 0L)
                                player.play()
                                viewModel.updateQueuedSongs(albumSongsList)
                                viewModel.updateSongIterator(i)
                                viewModel.updateAlbumArt(albumSongsList[i].albumArt)
                                viewModel.updateSongDuration((albumSongsList[i].time).toLong())
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
                            text = albumSongsList[i].name,
                            viewModel = viewModel
                        )
                        Spacer(
                            modifier = Modifier
                                .height(5.dp)
                        )
                        LcdText( // Artist name
                            text = albumSongsList[i].artist,
                            viewModel = viewModel
                        )
                        LcdText( // Album name
                            text = albumSongsList[i].album,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

//    TopAppBar(
//        title = {
//            LcdText(album, viewModel = viewModel)
//        },
//        modifier = Modifier
//            .fillMaxWidth(),
////            .height(6.dp),
//        navigationIcon = {
//            IconButton(
//                content = {
//                    painterResource(R.drawable.arrow_back)
//                },
//                onClick = {
//                    navController.popBackStack()
//                },
//                colors = IconButtonColors(
//                    contentColor = Color.White,
//                    containerColor = Color.Transparent,
//                    disabledContentColor = Color.White,
//                    disabledContainerColor = Color.White
//                )
//            )
//        },
//        windowInsets = WindowInsets.statusBars,
//        expandedHeight = 40.dp
//    )
}