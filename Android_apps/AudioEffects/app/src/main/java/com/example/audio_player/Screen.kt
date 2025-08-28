package com.example.audio_player

sealed class Screen(val route: String) {
    object Pager : Screen("pager")
    object AlbumSongsScreen : Screen("album_songs_screen")
}