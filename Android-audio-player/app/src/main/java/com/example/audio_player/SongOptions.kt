package com.example.audio_player

import android.media.browse.MediaBrowser
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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun SongOptions(songInfo: SongInfo, viewModel: PlayerViewModel, player: ExoPlayer) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .padding(5.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clickable(
                        onClick = {
                            player.addMediaItem(MediaItem.fromUri(songInfo.songUri))
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clickable(
                        onClick = {
                            player.removeMediaItems(player.mediaItemCount, player.currentMediaItemIndex)
                            player.removeMediaItems(player.currentMediaItemIndex - 1, -1)
                            player.addMediaItem(MediaItem.fromUri(songInfo.songUri))
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(R.drawable.queue_music),
                    contentDescription = "Add song to a new queue",
                    tint = viewModel.iconColor,
                )
                LcdText("Add song to a new queue", viewModel = viewModel)
            }
        }
    }
}