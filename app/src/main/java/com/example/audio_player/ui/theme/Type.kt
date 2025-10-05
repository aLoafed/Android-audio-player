package com.example.audio_player.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.audio_player.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val dotoFamily = FontFamily(
    Font(R.font.doto_rounded_thin, FontWeight.Thin),
    Font(R.font.doto_rounded_regular, FontWeight.Normal),
    Font(R.font.doto_light, FontWeight.Light),
)

val lcdFont = FontFamily(
    Font(R.font.ds_digii, FontWeight.Normal)
)

val orbitronFamily = FontFamily(
    Font(R.font.orbitron_regular, weight = FontWeight.Normal),
    Font(R.font.orbitron_medium, FontWeight.Medium)
)