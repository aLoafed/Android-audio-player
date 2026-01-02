package com.example.audio_player

import android.net.Uri
import androidx.compose.runtime.MutableFloatState
import androidx.compose.ui.graphics.ImageBitmap

data class VisualiserData(
    val visualiserList: DoubleArray,
    val volume: Double,
    val latency: Long
)

data class SongInfo(
    val name: String,
    val fileName: String,
    val songUri: Uri,
    val time: Float,
    val artist: String,
    val album: String,
    val albumArt: ImageBitmap
)

data class AlbumInfo(
    val albumName: String,
    val albumArt: ImageBitmap
)

data class TmpAudioEffectValue(var value: MutableFloatState)