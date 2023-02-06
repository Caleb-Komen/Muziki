package com.techdroidcentre.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.techdroidcentre.data.db.dao.PlayListDao
import com.techdroidcentre.data.db.dao.PlayListSongDao
import com.techdroidcentre.data.db.entity.PlayListEntity
import com.techdroidcentre.data.db.entity.PlayListSongEntity

@Database(entities = [PlayListEntity::class, PlayListSongEntity::class], version = 1)
abstract class PlayListDatabase: RoomDatabase() {
    abstract val playListDao: PlayListDao
    abstract val playListSongDao: PlayListSongDao

    companion object {
        const val DATABASE_NAME = "playlist_db"
    }
}