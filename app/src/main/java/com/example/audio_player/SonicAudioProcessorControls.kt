package com.example.audio_player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.audio_player.ui.theme.LcdGrey

@Composable
fun SonicAudioProcessorControls(viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LcdGrey),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

    }
}