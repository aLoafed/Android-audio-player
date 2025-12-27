package com.example.audio_player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController

@Composable
fun MoreOptions(viewModel: PlayerViewModel, mediaController: MediaController?) {
    Popup(
        onDismissRequest = {
            viewModel.showMoreOptions = !viewModel.showMoreOptions
        },
        properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        alignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.55f)
                .padding(5.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            AddSongToQueue(viewModel, mediaController)
        }
    }
}

@Composable
fun AddSongToQueue(viewModel: PlayerViewModel, mediaController: MediaController?) {
    val song = viewModel.moreOptionsSelectedSong
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(
                onClick = {
                    if (viewModel.queueingSongs) {
                        mediaController?.addMediaItem(MediaItem.fromUri(song.songUri))
                        viewModel.queuedSongs.add(song)
                    } else {
                        viewModel.queueingSongs = true
                        // Remove all songs except the currently playing one
                        viewModel.queuedSongs.removeAll { song ->
                            song != viewModel.queuedSongs[viewModel.songIndex]
                        }
                        viewModel.queuedSongs.add(song)
                        mediaController?.removeMediaItems(0, viewModel.songIndex)
                        mediaController?.removeMediaItems(
                            viewModel.songIndex + 1,
                            viewModel.queuedSongs.size
                        )
                        mediaController?.addMediaItem(MediaItem.fromUri(song.songUri))
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(R.drawable.queue_music),
            contentDescription = "Add song to queue",
            tint = viewModel.iconColor,
        )
        LcdText("Add song to queue", viewModel = viewModel)
    }
}