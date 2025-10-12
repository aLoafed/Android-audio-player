@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.example.audio_player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.navigation.NavController

@OptIn(UnstableApi::class)
@Composable
fun SonicAudioProcessorControls(
    viewModel: PlayerViewModel,
    spectrumAnalyzer: ForegroundNotificationService.SpectrumAnalyzer,
    navController: NavController,
    mediaController: MediaController?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        var speed by remember { mutableFloatStateOf(spectrumAnalyzer.speed) }
        var pitch by remember { mutableFloatStateOf(spectrumAnalyzer.pitch) }
        Spacer(modifier = Modifier.height(15.dp))
        // Speed control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LargeLcdText(
                "Change song's speed: ",
                viewModel = viewModel
            )
            Slider(
                value = speed,
                valueRange = 0f..2f,
                steps = 0, // For 0.5 increments
                modifier = Modifier
                    .size(140.dp, 20.dp),
                onValueChange = {
                    speed = it
                },
                thumb = {
                    SliderThumb(viewModel)
                },
                track = {
                    SettingsSliderTrack(viewModel)
                },
            )
            Spacer(modifier = Modifier.width(5.dp))
            LargeLcdText(
                "%.2f".format(spectrumAnalyzer.speed),
                viewModel = viewModel
            )
        }
        // Pitch control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LargeLcdText(
                "Change song's pitch: ",
                viewModel = viewModel
            )
            Slider(
                value = pitch,
                valueRange = 0f..2f,
                steps = 40, // For 0.5 increments
                modifier = Modifier
                    .size(140.dp, 20.dp),
                onValueChange = {
                    pitch = it
                },
                thumb = {
                    SliderThumb(viewModel)
                },
                track = {
                    SettingsSliderTrack(viewModel)
                },
            )
            Spacer(modifier = Modifier.width(5.dp))
            LargeLcdText(
                "%.2f".format(spectrumAnalyzer.pitch),
                viewModel = viewModel
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Apply changes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                modifier = Modifier
                    .size(50.dp),
                onClick = {
                    mediaController?.pause()
                    spectrumAnalyzer.speed = speed
                    spectrumAnalyzer.pitch = pitch
                    if (spectrumAnalyzer.pitch != 1f || spectrumAnalyzer.speed != 1f) {
                        spectrumAnalyzer.usingSonicProcessor = true
                    } else {
                        false
                    }
                    mediaController?.play()
                    navController.popBackStack()
                },
                content = {
                    Icon(
                        painterResource(R.drawable.done_tick),
                        contentDescription = "Apply changes"
                    )
                },
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = viewModel.iconColor,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
            )
        }
    }
}