package com.techdroidcentre.data.mapper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.techdroidcentre.data.util.*
import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song

fun Song.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        putLong(METADATA_KEY_MEDIA_ID, id)
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
        putString(METADATA_KEY_DURATION, duration.toString())
        putLong(METADATA_KEY_SIZE, size.toLong())
        putLong(METADATA_KEY_FLAG, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE.toLong())
    }.build()
}

fun Album.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        putLong(METADATA_KEY_MEDIA_ID, id)
        putString(METADATA_KEY_TITLE, name)
        putString(METADATA_KEY_ARTIST, artist)
        putString(METADATA_KEY_MEDIA_URI, uri)
        putLong(METADATA_KEY_NUM_TRACKS, numOfSongs.toLong())
        putLong(METADATA_KEY_FLAG, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE.toLong())
    }.build()
}

fun Artist.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder().apply {
        putLong(METADATA_KEY_MEDIA_ID, id)
        putString(METADATA_KEY_MEDIA_URI, uri)
        putString(METADATA_KEY_TITLE, name)
        putLong(METADATA_KEY_NUM_TRACKS, numOfSongs.toLong())
        putLong(METADATA_KEY_FLAG, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE.toLong())
    }.build()
}