package com.techdroidcentre.data.repository

import androidx.lifecycle.LiveData
import com.techdroidcentre.data.db.dao.PlayListSongDao
import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.model.PlayListSong
import javax.inject.Inject

class PlayListSongRepositoryImpl @Inject constructor(
    private val playlistSongDao: PlayListSongDao
): PlayListSongRepository {
    override fun getPlayListSongs(playListId: Long): LiveData<List<PlayListSong>> {
        TODO("Not yet implemented")
    }

    override suspend fun addSong(playlistId: Long, playListSong: PlayListSong) {
        playlistSongDao.addSong(playListSong.toEntity(playlistId))
    }

    override suspend fun deleteSong(id: String) {
        playlistSongDao.deleteSong(id)
    }
}