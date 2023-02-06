package com.techdroidcentre.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlayListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String
)
