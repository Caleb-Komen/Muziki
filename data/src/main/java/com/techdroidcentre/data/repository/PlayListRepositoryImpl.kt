package com.techdroidcentre.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.techdroidcentre.data.db.dao.PlayListDao
import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.data.model.PlayList
import javax.inject.Inject

class PlayListRepositoryImpl @Inject constructor(
    private val playlistDao: PlayListDao
): PlayListRepository {
    override fun getPlayLists(): LiveData<List<PlayList>> {
        return Transformations.map(playlistDao.getPlayLists()) { playlists ->
            playlists.map { it.toModel() }
        }
    }

    override suspend fun createPlaylist(playList: PlayList) {
        playlistDao.createPlaylist(playList.toEntity())
    }

    override suspend fun updatePlayList(playList: PlayList) {
        playlistDao.updatePlayList(playList.toEntity())
    }

    override suspend fun deletePlayList(id: Long) {
        playlistDao.deletePlayList(id)
    }
}