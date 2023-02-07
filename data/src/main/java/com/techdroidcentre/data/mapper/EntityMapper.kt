package com.techdroidcentre.data.mapper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.techdroidcentre.data.db.entity.PlayListEntity
import com.techdroidcentre.data.db.entity.PlayListSongEntity
import com.techdroidcentre.data.model.PlayList
import com.techdroidcentre.data.model.PlayListSong
import com.techdroidcentre.data.util.*
import com.techdroidcentre.domain.models.Song

fun Song.toMediaMetadataCompat(): MediaMetadataCompat {

    return MediaMetadataCompat.Builder().apply {
        putString(METADATA_KEY_MEDIA_ID, id.toString())
        putString(METADATA_KEY_TITLE, title)
        putLong(METADATA_KEY_ARTIST_ID, artistId)
        putString(METADATA_KEY_ARTIST, artist)
        putLong(METADATA_KEY_ALBUM_ID, albumId)
        putString(METADATA_KEY_ALBUM, album)
        putString(METADATA_KEY_DISPLAY_TITLE, title)
        putString(METADATA_KEY_DISPLAY_SUBTITLE, artist)
        putString(METADATA_KEY_DISPLAY_DESCRIPTION, album)
        putString(METADATA_KEY_MEDIA_URI, uri)
        putString(METADATA_KEY_PATH, path)
        putLong(METADATA_KEY_DURATION, duration)
        putLong(METADATA_KEY_SIZE, size.toLong())
        putString(METADATA_KEY_ART_URI, coverArt)
        putLong(METADATA_KEY_FLAG, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE.toLong())
    }.build()
}

fun PlayListSong.toEntity(playListId: Long): PlayListSongEntity {
    return PlayListSongEntity(
        id = id,
        mediaUri = mediaUri,
        title = title,
        album = album,
        artist = artist,
        artUri = artUri,
        playListId = playListId
    )
}

fun PlayListSongEntity.toModel(): PlayListSong {
    return PlayListSong(
        id = id,
        mediaUri = mediaUri,
        title = title,
        album = album,
        artist = artist,
        artUri = artUri
    )
}

fun PlayList.toEntity(): PlayListEntity {
    return PlayListEntity(id = id, name = name)
}

fun PlayListEntity.toModel(): PlayList {
    return PlayList(id = id, name = name)
}