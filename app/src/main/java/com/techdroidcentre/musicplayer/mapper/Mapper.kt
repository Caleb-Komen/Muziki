package com.techdroidcentre.musicplayer.mapper

import com.techdroidcentre.data.model.PlayList
import com.techdroidcentre.data.model.PlayListSong
import com.techdroidcentre.musicplayer.model.PlayListViewState
import com.techdroidcentre.musicplayer.model.SongData

fun PlayListViewState.toModel(): PlayList {
    return PlayList(id = id, name = name)
}

fun PlayList.toViewState(): PlayListViewState {
    return PlayListViewState(id = id, name = name)
}

fun PlayListSong.toSongData(): SongData {
    return SongData(
        mediaId = id,
        uri = mediaUri,
        title = title,
        subtitle = artist,
        description = album,
        coverArt = null
    )
}

fun SongData.toPlaylistSong(): PlayListSong {
    return PlayListSong(
        id = mediaId,
        mediaUri = uri,
        title = title,
        artist = subtitle,
        album = description,
        artUri = ""
    )
}