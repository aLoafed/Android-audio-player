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
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.media3.common.util.UnstableApi
import com.example.audio_player.ui.theme.LcdGrey
import kotlin.math.round
import kotlin.math.truncate

@OptIn(UnstableApi::class)
@Composable
fun SonicAudioProcessorControls(viewModel: PlayerViewModel, spectrumAnalyzer: ForegroundNotificationService.SpectrumAnalyzer) {
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
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var speed by remember { mutableFloatStateOf(spectrumAnalyzer.speed) }
            LargeLcdText(
                "Change song's speed: ",
                viewModel = viewModel
            )
            Slider(
                value = speed,
                valueRange = 0f..2f,
                modifier = Modifier
                    .size(140.dp, 20.dp),
                onValueChange = {
                    speed = it
                    spectrumAnalyzer.speed = it
                    spectrumAnalyzer.usingSonicProcessor = it != 1f
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var pitch by remember { mutableFloatStateOf(spectrumAnalyzer.pitch) }
            LargeLcdText(
                "Change song's pitch: ",
                viewModel = viewModel
            )
            Slider(
                value = pitch,
                valueRange = 0f..2f,
                modifier = Modifier
                    .size(140.dp, 20.dp),
                onValueChange = {
                    pitch = it
                    spectrumAnalyzer.pitch = it
                    spectrumAnalyzer.usingSonicProcessor = it != 1f
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
    }
}