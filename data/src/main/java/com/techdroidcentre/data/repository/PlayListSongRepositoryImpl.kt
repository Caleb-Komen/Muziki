package com.techdroidcentre.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.techdroidcentre.data.db.dao.PlayListSongDao
import com.techdroidcentre.data.mapper.toEntity
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.data.model.PlayListSong
import javax.inject.Inject

class PlayListSongRepositoryImpl @Inject constructor(
    private val playlistSongDao: PlayListSongDao
): PlayListSongRepository {
    override fun getPlayListSongs(playListId: Long): LiveData<List<PlayListSong>> {
        return Transformations.map(playlistSongDao.getPlayListSongs(playListId)) { playlistSongs ->
            playlistSongs.map { it.toModel() }
        }
    }

    override suspend fun addSong(playlistId: Long, playListSong: PlayListSong) {
        playlistSongDao.addSong(playListSong.toEntity(playlistId))
    }

    override suspend fun deleteSong(mediaUri: String) {
        playlistSongDao.deleteSong(mediaUri)
    }
}