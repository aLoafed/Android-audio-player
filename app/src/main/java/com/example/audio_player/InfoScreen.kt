package com.example.audio_player

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio_player.ui.theme.lcdFont

@Composable
fun InfoScreen(viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
////                TODO("Send intent to browser to open link")
//                ActivityResultContracts.StartActivityForResult()
//                    .parseResult(
//                        1,
//                        Intent(Intent.A)
//                    )
            },
        ) {
            Text(
                modifier = Modifier,
                text = "github.com/aLoafed/Android-audio-player",
                color = viewModel.textColor,
                fontSize = 20.sp,
                fontFamily = lcdFont,
                fontWeight = FontWeight.Normal,
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

    }
}