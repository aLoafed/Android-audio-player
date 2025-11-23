package com.example.audio_player

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.merge
import java.io.FileNotFoundException

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

@OptIn(UnstableApi::class)
fun getSongInfo(context: Context): List<SongInfo> {
    val songInfo = mutableListOf<SongInfo>()
    val externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media._ID
    )
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(
        externalUri,
        projection,
        MediaStore.Audio.Media.IS_MUSIC,
        null,
        sortOrder
    )
    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val fileNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        while (it.moveToNext()) {
            val getName = it.getString(nameColumn)
            val getFileName = it.getString(fileNameColumn)
            val getAlbum = it.getString(albumColumn)
            val getArtist = it.getString(artistColumn)
            var getDuration = it.getDouble(durationColumn)
            val getId = it.getLong(idColumn)
            getDuration /= 1000
            val duration = getDuration.toFloat()
            val songUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, getId
            )
            var albumCover = BitmapFactory.decodeResource(context.resources, R.drawable.album_art_not_found)
            try {
                albumCover = contentResolver.loadThumbnail(
                    songUri,
                    android.util.Size(500,500),
                    null
                )
            } catch (e: FileNotFoundException) {}
            songInfo.add(
                SongInfo(
                    getName,
                    getFileName,
                    songUri,
                    duration,
                    getArtist,
                    getAlbum,
                    albumCover.asImageBitmap()
                )
            )
        }
    }
//    val mergeSort = MergeSort()
    return songInfo
}
@OptIn(UnstableApi::class)
fun getAlbumSongInfo(context: Context): List<AlbumInfo> {
    val albumInfo = mutableListOf<AlbumInfo>()
    val externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Albums._ID
    )
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(
        externalUri,
        projection,
        MediaStore.Audio.Media.IS_MUSIC,
        null,
        sortOrder
    )
    cursor?.use {
        val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        while (it.moveToNext()) {
            var newAlbumEntry = true // Used for checking whether the songs album is new to prevent dupes
            val getAlbum = it.getString(albumColumn)
            val getId = it.getLong(idColumn)
            val songUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, getId
            )
            var albumCover = BitmapFactory.decodeResource(context.resources, R.drawable.album_art_not_found)
            try {
                albumCover = contentResolver.loadThumbnail(
                    songUri,
                    android.util.Size(500,500),
                    null
                )
            } catch (e: FileNotFoundException) {}
            for (i in 0 until albumInfo.count()){
                if (albumInfo[i].albumName == getAlbum) {
                    newAlbumEntry = false
                    break
                }
            }
            if (newAlbumEntry) {
                albumInfo.add(
                    AlbumInfo(
                        getAlbum,
                        albumCover.asImageBitmap()
                    )
                )
            }
        }
    }
//    val mergeSort = MergeSort()
    return albumInfo
}
