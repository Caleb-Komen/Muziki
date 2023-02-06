package com.techdroidcentre.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techdroidcentre.data.db.entity.PlayListSongEntity

@Dao
interface PlayListSongDao {
    @Query("SELECT * FROM playlist_songs WHERE playlist_id = :playListId")
    fun getPlayListSongs(playListId: Long): LiveData<List<PlayListSongEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSong(playListSongEntity: PlayListSongEntity)

    @Query("DELETE FROM playlist_songs WHERE id = :id")
    suspend fun deleteSong(id: String)
}