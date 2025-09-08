@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.audio_player

import androidx.annotation.OptIn
import androidx.collection.intListOf
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.coroutineScope
@OptIn(UnstableApi::class)
@Composable
fun NavHost(
    player: ExoPlayer,
    songInfo: List<SongInfo>,
    spectrumAnalyzer: SpectrumAnalyzer,
    viewModel: PlayerViewModel,
    albumInfo: List<AlbumInfo>
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Pager.route,
    ) {
        composable(route = Screen.Pager.route) {
            Pager(player, spectrumAnalyzer, viewModel, songInfo, albumInfo, navController)
        }
        composable(route = Screen.AlbumSongsScreen.route) {
            AlbumSongsScreen(viewModel.selectedAlbum, songInfo, player, viewModel, navController)
        }
        composable(route = Screen.Settings.route) {
            Settings(navController, viewModel)
        }
        composable(route = Screen.ThemeChange.route) {
            ThemeChange(viewModel, navController)
        }
        composable(route = Screen.ColorPicker.route) {
            ColorPicker(viewModel, navController)
        }
    }
}
@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
@Composable
fun Pager(
    player: ExoPlayer,
    spectrumAnalyzer: SpectrumAnalyzer,
    viewModel: PlayerViewModel,
    songInfo: List<SongInfo>,
    albumInfo: List<AlbumInfo>,
    navController: NavController
) {
    val pagerState = rememberPagerState(
        initialPage = 0
    ) {
        3
    }
    var selectedTab by remember {
        mutableIntStateOf(pagerState.currentPage)
    }
    val iconList = intListOf(
        R.drawable.play_arrow, R.drawable.outline_play_arrow, R.drawable.library_music, R.drawable.outline_library_music, R.drawable.album, R.drawable.outline_album
    )
    LaunchedEffect(selectedTab) { // Controls tabRow inputs
        pagerState.animateScrollToPage(
            page = selectedTab,
            animationSpec = tween(
                200,
                0,
                LinearEasing
            )
        )
    }
    LaunchedEffect(pagerState.currentPage) { // Controls screen swipes
        selectedTab = pagerState.currentPage
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            TabRow(
                modifier = Modifier
                    .fillMaxWidth() // Was 0.85f
                    .height(55.dp),
                containerColor = viewModel.backgroundColor,
                selectedTabIndex = selectedTab,
                indicator =  { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(
                                tabPositions[pagerState.currentPage]
                            ),
                        color = viewModel.iconColor,
                        height = 2.dp
                    )
                }
            ) {
                for (i in 0 until pagerState.pageCount) {
                    Tab(
                        modifier = Modifier
                            .fillMaxSize(),
                        selected = selectedTab == i,
                        onClick = {
                            selectedTab = i
                        },
                        icon = {
                            if (selectedTab == i) {
                                Icon(
                                    painter = painterResource(iconList[i * 2]),
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    painter = painterResource(iconList[i * 2 + 1]),
                                    contentDescription = null
                                )
                            }
                        },
                        selectedContentColor = viewModel.iconColor,
                        unselectedContentColor = viewModel.iconColor,
                    )
                }
                var dropDownMenu by remember { mutableStateOf(false) }
                Tab(
                    modifier = Modifier
                        .fillMaxSize(0.15f),
                    selected = false,
                    onClick = {
                        dropDownMenu = !dropDownMenu
                    },
                    content = {
                        Icon(
                            painterResource(R.drawable.more_menu),
                            contentDescription = "More options"
                        )
                        DropdownMenu(
                            containerColor = viewModel.backgroundColor,
                            expanded = dropDownMenu,
                            onDismissRequest = { dropDownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    LcdText(
                                        "Settings",
                                        viewModel = viewModel
                                    )
                                },
                                onClick = {
                                    navController.navigate("settings")
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    LcdText(
                                        "Equaliser",
                                        viewModel = viewModel
                                    )
                                },
                                onClick = {TODO()}
                            )
                            DropdownMenuItem(
                                text = {
                                    LcdText(
                                        "Info",
                                        viewModel = viewModel
                                    )
                                },
                                onClick = {TODO()}
                            )
                        }
                    },
                    selectedContentColor = viewModel.iconColor,
                    unselectedContentColor = viewModel.iconColor
                )
            }
        }
        HorizontalPager(
            state = pagerState
        ) { currentPage ->
            when (currentPage) {
                0 -> PlayerScreen(player, spectrumAnalyzer, viewModel, songInfo)
                1 -> SongsScreen(songInfo, player, viewModel, pagerState, spectrumAnalyzer)
                2 -> AlbumScreen(albumInfo, songInfo, player, viewModel, pagerState, navController)
            }
        }
    }
}
