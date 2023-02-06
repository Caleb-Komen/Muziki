package com.techdroidcentre.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PlayListWithSongsEntity(
    @Embedded
    val playListEntity: PlayListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlist_id"
    )
    val playListSongsEntity: List<PlayListSongEntity>
)
