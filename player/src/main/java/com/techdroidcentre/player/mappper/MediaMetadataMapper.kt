package com.techdroidcentre.player.mappper

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.google.android.exoplayer2.util.MimeTypes
import com.techdroidcentre.data.util.METADATA_KEY_PATH
import com.techdroidcentre.data.util.METADATA_KEY_SIZE

fun MediaMetadataCompat.toMediaItem(): com.google.android.exoplayer2.MediaItem {
    return com.google.android.exoplayer2.MediaItem.Builder()
        .setMediaId(getString(METADATA_KEY_MEDIA_ID))
        .setUri(getString(METADATA_KEY_MEDIA_URI))
        .setMimeType(MimeTypes.AUDIO_MPEG)
        .setMediaMetadata(toMediaItemMetaData())
        .build()
}

fun MediaMetadataCompat.toMediaItemMetaData(): com.google.android.exoplayer2.MediaMetadata {
    val extras = Bundle().apply {
        putLong(METADATA_KEY_DURATION, getLong(METADATA_KEY_DURATION))
        putString(METADATA_KEY_PATH, getString(METADATA_KEY_PATH))
        putInt(METADATA_KEY_SIZE, getLong(METADATA_KEY_SIZE).toInt())
    }
    return com.google.android.exoplayer2.MediaMetadata.Builder()
        .setTitle(getString(METADATA_KEY_TITLE))
        .setAlbumTitle(getString(METADATA_KEY_ALBUM))
        .setArtist(getString(METADATA_KEY_ARTIST))
        .setDisplayTitle(getString(METADATA_KEY_DISPLAY_TITLE))
        .setDescription(getString(METADATA_KEY_DISPLAY_DESCRIPTION))
        .setExtras(extras)
        .build()
}