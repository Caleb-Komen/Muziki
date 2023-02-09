package com.techdroidcentre.data.repository

import androidx.lifecycle.LiveData
import com.techdroidcentre.data.model.PlayListSong

interface PlayListSongRepository {
    fun getPlayListSongs(playListId: Long): LiveData<List<PlayListSong>>

    suspend fun addSong(playlistId: Long, playListSong: PlayListSong)

    suspend fun deleteSong(mediaUri: String)
}