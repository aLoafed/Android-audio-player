package com.example.audio_player

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

class MergeSort {
    @JvmName ("albumSort")
    fun sort(albumInfo: MutableList<AlbumInfo>): MutableList<AlbumInfo> {
        // Base case
        if (albumInfo.size == 1) {
            return albumInfo
        } else {
            val midpoint = albumInfo.size / 2
            var left = albumInfo.subList(0, midpoint)
            var right = albumInfo.subList(midpoint, albumInfo.size)
            left = sort(left)
            right = sort(right)
            val sorted = merge(left, right)
            //Log.d("MusicPlayer", "Sorted: $sorted")
            return sorted
        }
    }
    @OptIn(UnstableApi::class)
    fun sort(songInfo: MutableList<SongInfo>): MutableList<SongInfo> {
        // Base case
        if (songInfo.size == 1) {
            return songInfo
        } else {
            val midpoint = songInfo.size / 2
            var left = songInfo.subList(0, midpoint)
            var right = songInfo.subList(midpoint, songInfo.size)  // Fixed: was songInfo.size - 1
            left = sort(left)
            right = sort(right)
            val sorted = merge(left, right)
            Log.d("MusicPlayer", "Sorted: $sorted")
            return sorted
        }
    }

    @OptIn(UnstableApi::class)
    @JvmName ("albumMerge")
    private fun merge(left: MutableList<AlbumInfo>, right: MutableList<AlbumInfo>): MutableList<AlbumInfo> {
        val mergedList = mutableListOf<AlbumInfo>()
        var leftIndex = 0
        var rightIndex = 0

        while (leftIndex < left.size || rightIndex < right.size) {
            try {
                Log.d("MusicPlayer", "Sorted: $mergedList")
                if (left[leftIndex].albumName < right[rightIndex].albumName) {
                    mergedList.add(left[leftIndex])
                    leftIndex++
                } else {
                    mergedList.add(right[rightIndex])
                    rightIndex++
                }
            } catch (e: IndexOutOfBoundsException) {
                if (leftIndex >= left.size) {
                    for (i in rightIndex until right.size) {
                        mergedList.add(right[i])
                        rightIndex ++
                    }
                } else {
                    for (i in leftIndex until left.size) {
                        mergedList.add(left[i])
                        leftIndex ++
                    }
                }
            }
        }

        return mergedList
    }

    @OptIn(UnstableApi::class)
    private fun merge(left: MutableList<SongInfo>, right: MutableList<SongInfo>): MutableList<SongInfo> {
        val mergedList = mutableListOf<SongInfo>()
        var leftIndex = 0
        var rightIndex = 0

        while (leftIndex < left.size && rightIndex < right.size) {
            try {
                Log.d("MusicPlayer", "Sorted: $mergedList")
                if (left[leftIndex].name <= right[rightIndex].name) {
                    mergedList.add(left[leftIndex])
                    leftIndex++
                } else {
                    mergedList.add(right[rightIndex])
                    rightIndex++
                }
            } catch (e: IndexOutOfBoundsException) {
                if (leftIndex >= left.size) {
                    for (i in rightIndex until right.size) {
                        mergedList.add(right[i])
                        rightIndex ++
                    }
                } else {
                    for (i in leftIndex until left.size) {
                        mergedList.add(left[i])
                        leftIndex ++
                    }
                }
            }
        }

        return mergedList
    }
}