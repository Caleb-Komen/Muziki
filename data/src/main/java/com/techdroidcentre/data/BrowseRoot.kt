package com.techdroidcentre.data

import android.content.Context
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.techdroidcentre.data.util.METADATA_KEY_ALBUM_ID
import com.techdroidcentre.data.util.METADATA_KEY_ARTIST_ID
import com.techdroidcentre.data.util.METADATA_KEY_FLAG

const val BROWSABLE_ROOT = "/"
const val ALBUMS_ROOT = "ALBUMS"
const val ARTISTS_ROOT = "ARTISTS"
const val SONGS_ROOT = "SONGS"
const val RESOURCE_ROOT_URI = "android.resource://com.techdroidcentre.data/drawable/"

class BrowseRoot(
    context: Context,
    musicSource: MusicSource
) {
    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    init {
        val rootList = mediaIdToChildren[BROWSABLE_ROOT] ?: mutableListOf()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_MEDIA_ID, ALBUMS_ROOT)
            putString(METADATA_KEY_TITLE, context.getString(R.string.albums_title))
            putString(METADATA_KEY_ALBUM_ART_URI, RESOURCE_ROOT_URI + context.resources.getResourceEntryName(R.drawable.ic_baseline_album_24))
            putLong(METADATA_KEY_FLAG, FLAG_BROWSABLE.toLong())
        }.build()

        val artistsMetadata = MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_MEDIA_ID, ARTISTS_ROOT)
            putString(METADATA_KEY_TITLE, context.getString(R.string.artists_title))
            putString(METADATA_KEY_ALBUM_ART_URI, RESOURCE_ROOT_URI + context.resources.getResourceEntryName(R.drawable.ic_account_music))
            putLong(METADATA_KEY_FLAG, FLAG_BROWSABLE.toLong())
        }.build()

        val songsMetadata = MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_MEDIA_ID, SONGS_ROOT)
            putString(METADATA_KEY_TITLE, context.getString(R.string.songs_title))
            putString(METADATA_KEY_ALBUM_ART_URI, RESOURCE_ROOT_URI + context.resources.getResourceEntryName(R.drawable.ic_baseline_music_note_24))
            putLong(METADATA_KEY_FLAG, FLAG_BROWSABLE.toLong())
        }.build()

        rootList += albumsMetadata
        rootList += artistsMetadata
        rootList += songsMetadata

        mediaIdToChildren[BROWSABLE_ROOT] = rootList

        musicSource.albums.forEach { mediaMetadata ->
            val albumsRoot = mediaIdToChildren[ALBUMS_ROOT] ?: mutableListOf()
            albumsRoot += mediaMetadata
            mediaIdToChildren[ALBUMS_ROOT] = albumsRoot
        }

        musicSource.artists.forEach { mediaMetadata ->
            val artistsRoot = mediaIdToChildren[ARTISTS_ROOT] ?: mutableListOf()
            artistsRoot += mediaMetadata
            mediaIdToChildren[ARTISTS_ROOT] = artistsRoot
        }

        musicSource.songs.forEach { mediaMetadata ->
            val songsChildren = mediaIdToChildren[SONGS_ROOT] ?: mutableListOf()
            songsChildren += mediaMetadata
            mediaIdToChildren[SONGS_ROOT] = songsChildren

            val albumId = mediaMetadata.getLong(METADATA_KEY_ALBUM_ID)
            val albumChildren = mediaIdToChildren[albumId.toString()] ?: mutableListOf()
            albumChildren += mediaMetadata
            mediaIdToChildren[albumId.toString()]

            val artistId = mediaMetadata.getLong(METADATA_KEY_ARTIST_ID)
            val artistChildren = mediaIdToChildren[artistId.toString()] ?: mutableListOf()
            artistChildren += mediaMetadata
            mediaIdToChildren[artistId.toString()]
        }
    }

}