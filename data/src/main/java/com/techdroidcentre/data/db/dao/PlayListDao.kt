package com.techdroidcentre.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.techdroidcentre.data.db.entity.PlayListEntity

@Dao
interface PlayListDao {
    @Query("SELECT * FROM playlists")
    fun getPlayLists(): LiveData<List<PlayListEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createPlaylist(playListEntity: PlayListEntity)

    @Update
    suspend fun updatePlayList(playListEntity: PlayListEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlayList(id: Long)
}