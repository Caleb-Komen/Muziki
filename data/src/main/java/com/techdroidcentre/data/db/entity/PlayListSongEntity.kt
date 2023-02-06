package com.techdroidcentre.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_songs",
    foreignKeys = [
        ForeignKey(
            entity = PlayListEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = CASCADE
        )
    ]
)
data class PlayListSongEntity(
    @PrimaryKey
    val id: String,
    val mediaUri: String,
    val title: String,
    val album: String,
    val artist: String,
    val artUri: String,
    @ColumnInfo(name = "playlist_id")
    val playListId: Long
)
