package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListPrefetchStrategy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@OptIn(UnstableApi::class)
@Composable
fun SongsScreen(
    songInfo: List<SongInfo>,
    mediaController: MediaController?,
    viewModel: PlayerViewModel,
    pagerState: PagerState,
    navController: NavController
) {
    val fetchStrategy = LazyListPrefetchStrategy(50)
    val lazyColumnState = rememberLazyListState(
        initialFirstVisibleItemIndex = 0,
        initialFirstVisibleItemScrollOffset = 0,
        prefetchStrategy = fetchStrategy
    )
    val lazyColumnSize = songInfo.count()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
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
            items(lazyColumnSize) { i ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(5.dp)
                        .clickable(
                            onClick = {
                                viewModel.updateShuffleMode(false)
                                mediaController?.clearMediaItems()
                                for (j in 0 until songInfo.count()) {
                                    mediaController?.addMediaItem(MediaItem.fromUri(songInfo[j].songUri))
                                }
                                mediaController?.prepare()
                                mediaController?.seekTo(i, 0L)
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
                    IconButton(
                        modifier = Modifier
                            .size(50.dp),
                        content = {
                            Icon(
                                painter = painterResource(R.drawable.more_menu),
                                contentDescription = "More options",
                            )
                        },
                        onClick = {
                            viewModel.updateSelectedOptionsSong(songInfo[i])
                            navController.navigate("song_options")
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = viewModel.iconColor,
                            disabledContentColor = viewModel.iconColor
                        )
                    )
                }
            }
        }
        ScrollBar(lazyColumnState, viewModel, lazyColumnSize.toFloat())
    }
}

@Composable
fun ScrollBar(columnState: LazyListState, viewModel: PlayerViewModel, lazyColumnSize: Float, itemsPerViewport: Float = 9f) {
    var scrollBarHeight by remember { mutableFloatStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.displayCutout)
            .onGloballyPositioned { coordinates ->
                scrollBarHeight = coordinates.size.height.toFloat()
            },
    ) {
        val scope = rememberCoroutineScope()
        val tabOffset = remember {
            derivedStateOf {
                if (lazyColumnSize <= itemsPerViewport) {
                    0f
                } else {
                    // (Percentage of lazy list covered + percentage of offset) * scrollBarHeight
                    val overallPercentageScroll = columnState.firstVisibleItemIndex.toFloat() / columnState.layoutInfo.totalItemsCount.toFloat()
                    val offsetPercentageScroll = columnState.firstVisibleItemScrollOffset.dp.value / (columnState.layoutInfo.viewportSize.height.toFloat() * (columnState.layoutInfo.totalItemsCount.toFloat() / itemsPerViewport))
                    overallPercentageScroll * scrollBarHeight + offsetPercentageScroll * scrollBarHeight
                }
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { pointerChange, value ->
                        val initialLocale = tabOffset
                        // tabOffset = pointerChange.position.y; was here, may need to revert
                        val yDelta = pointerChange.position.y
                        scope.launch {
                            columnState.scrollBy(
                                // Percentage change in position * total lazy column size in px
                                (yDelta - initialLocale.value) / scrollBarHeight * (columnState.layoutInfo.viewportSize.height.toFloat() * (columnState.layoutInfo.totalItemsCount.toFloat() / itemsPerViewport))
                            )
                        }
                    }
                }
        ) {
            val tabHeight = if (lazyColumnSize <= itemsPerViewport) {
                scrollBarHeight
            } else {
                itemsPerViewport / lazyColumnSize * scrollBarHeight
            }
            drawRoundRect(
                topLeft = Offset(0f,tabOffset.value.coerceIn(0f, scrollBarHeight - tabHeight)),
                color = viewModel.backgroundColor.increaseBrightness(0.05f), // Should probably change the color
                size = Size(30f, tabHeight - 10f),
                cornerRadius = CornerRadius(30f, 30f),
            )
        }
    }
}
