package com.example.audio_player

data class EQDataClass(
    val eqList: DoubleArray,
    val volume: Double,
    val latency: Long
)
