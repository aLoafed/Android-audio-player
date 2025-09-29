package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController

@OptIn(UnstableApi::class)
@Composable
fun AlbumScreen(
    albumInfo: List<AlbumInfo>,
    viewModel: PlayerViewModel,
    navController: NavController
) {
    val rowNumbers = (
            if (albumInfo.count() % 3 != 0) {
                albumInfo.count() / 3 + 1
            } else {
                albumInfo.count() / 3
            }
            )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        items(rowNumbers) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (albumInfo.count() % 3 == 0) {
                    for (index in 0..2) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .fillMaxHeight()
                                .width(120.dp)
                                .clickable(
                                    onClick = {
                                        viewModel.updateSelectedAlbum(albumInfo[rowIndex * 3 + index].albumName)
                                        navController.navigate("album_songs_screen")
                                    }
                                ),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Image(
                                modifier = Modifier
                                    .aspectRatio(1f),
                                bitmap = albumInfo[rowIndex * 3 + index].albumArt,
                                contentDescription = null
                            )
                            AlbumScreenLcdText(
                                albumInfo[rowIndex * 3 + index].albumName,
                                viewModel = viewModel,
                            )
                        }
                    }
                } else {
                    if (rowNumbers != rowIndex + 1) {
                        for (index in 0..2) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                                    .fillMaxHeight()
                                    .width(120.dp)
                                    .clickable(
                                        onClick = {
                                            viewModel.updateSelectedAlbum(albumInfo[rowIndex * 3 + index].albumName)
                                            navController.navigate("album_songs_screen")
                                        }
                                    ),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Image(
                                    modifier = Modifier
                                        .aspectRatio(1f),
                                    bitmap = albumInfo[rowIndex * 3 + index].albumArt,
                                    contentDescription = null
                                )
                                AlbumScreenLcdText(
                                    albumInfo[rowIndex * 3 + index].albumName,
                                    viewModel = viewModel,
                                )
                            }
                        }
                    } else {
                        for (index in 0..(albumInfo.count() % 3)) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                                    .fillMaxHeight()
                                    .width(120.dp)
                                    .clickable(
                                        onClick = {
                                            viewModel.updateSelectedAlbum(albumInfo[rowIndex * 3 + index].albumName)
                                            navController.navigate("album_songs_screen")
                                        }
                                    ),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Image(
                                    modifier = Modifier
                                        .aspectRatio(1f),
                                    bitmap = albumInfo[rowIndex * 3 + index - 1].albumArt,
                                    contentDescription = null
                                )
                                AlbumScreenLcdText(
                                    albumInfo[rowIndex * 3 + index - 1].albumName,
                                    viewModel = viewModel,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}