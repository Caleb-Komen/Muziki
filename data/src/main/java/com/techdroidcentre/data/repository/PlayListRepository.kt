package com.techdroidcentre.data.repository

import androidx.lifecycle.LiveData
import com.techdroidcentre.data.model.PlayList

interface PlayListRepository {
    fun getPlayLists(): LiveData<List<PlayList>>

    suspend fun createPlaylist(playList: PlayList)

    suspend fun updatePlayList(playList: PlayList)

    suspend fun deletePlayList(id: Long)
}