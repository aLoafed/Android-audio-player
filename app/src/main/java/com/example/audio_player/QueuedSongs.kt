package com.example.audio_player

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyListPrefetchStrategy
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController

@ExperimentalFoundationApi
@Composable
fun SongQueue(viewModel: PlayerViewModel, mediaController: MediaController?, songInfo: List<SongInfo>) {
    var tabScrollOffset by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
        }
    }
    NestedScrollDispatcher()
    val fetchStrategy = LazyListPrefetchStrategy(50)
    val lazyColumnState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0,
        initialFirstVisibleItemScrollOffset = 0,
        prefetchStrategy = fetchStrategy
    )
    val lazyListSize = viewModel.queuedSongs.count()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .nestedScroll(nestedScrollConnection),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.955f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            state = lazyColumnState,
        ) {
            items(lazyListSize) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .border(
                            width = (
                                    if (i == viewModel.songIterator) {
                                        0.dp
                                    } else {
                                        (-1).dp
                                    }
                                    ),
                            color = viewModel.iconColor,
                            shape = RoundedCornerShape(corner = CornerSize(10.dp))
                        )
                        .padding(5.dp)
                        .clickable(
                            onClick = {
                                mediaController?.clearMediaItems()
                                for (j in 0 until viewModel.queuedSongs.count()) {
                                    mediaController?.addMediaItem(MediaItem.fromUri(viewModel.queuedSongs[j].songUri))
                                }
                                mediaController?.prepare()
                                mediaController?.seekTo(i, 0L)
                                mediaController?.play()
                                viewModel.updateAlbumArt(viewModel.queuedSongs[i].albumArt)
                                viewModel.updateSongDuration((viewModel.queuedSongs[i].time).toLong())
                                viewModel.updateSongIterator(i)
                                viewModel.updatePlayingFromSongsScreen(true)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image( // Album art
                        bitmap = viewModel.queuedSongs[i].albumArt,
                        modifier = Modifier
                            .size(60.dp),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
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
                            text = viewModel.queuedSongs[i].name,
                            viewModel = viewModel
                        )
                        Spacer(
                            modifier = Modifier
                                .height(5.dp)
                        )
                        LcdText( // Artist name
                            text = viewModel.queuedSongs[i].artist,
                            viewModel = viewModel
                        )
                        LcdText( // Album name
                            text = viewModel.queuedSongs[i].album,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
        ScrollBar(lazyColumnState, viewModel, lazyListSize.toFloat())
    }
}