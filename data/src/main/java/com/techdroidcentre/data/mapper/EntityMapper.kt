package com.techdroidcentre.data.mapper

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
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
        putLong(METADATA_KEY_FLAG, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE.toLong())
    }.build()
}