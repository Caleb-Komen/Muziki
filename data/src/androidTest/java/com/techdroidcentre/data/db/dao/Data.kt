package com.techdroidcentre.data.db.dao

import com.techdroidcentre.data.db.entity.PlayListEntity
import com.techdroidcentre.data.db.entity.PlayListSongEntity
import com.techdroidcentre.data.db.entity.PlayListWithSongsEntity

val playlistWithSongs1 = PlayListWithSongsEntity(
    playListEntity = PlayListEntity(id = 1L, name = "Country Music"),
    playListSongsEntity = listOf(
        PlayListSongEntity(
            id = "101",
            mediaUri = "content://media/external/audio/101",
            title = "Jolene",
            album = "Country Essentials",
            artist = "Dolly Parton",
            artUri = "",
            playListId = 1L
        ),
        PlayListSongEntity(
            id = "102",
            mediaUri = "content://media/external/audio/102",
            title = "Do I",
            album = "Country Ballads Essentials",
            artist = "Luke Bryan",
            artUri = "",
            playListId = 1L
        )
    )
)
val playlistWithSongs2 = PlayListWithSongsEntity(
    playListEntity = PlayListEntity(id = 2L, name = "Indie Folk"),
    playListSongsEntity = listOf(
        PlayListSongEntity(
            id = "103",
            mediaUri = "content://media/external/audio/103",
            title = "Opaline",
            album = "Cannot Be, Whatsoever",
            artist = "Novo Amor",
            artUri = "",
            playListId = 2L
        ),
        PlayListSongEntity(
            id = "104",
            mediaUri = "content://media/external/audio/104",
            title = "Misty",
            album = "Misty",
            artist = "Caamp",
            artUri = "",
            playListId = 2L
        )
    )
)