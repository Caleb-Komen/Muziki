package com.techdroidcentre.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.techdroidcentre.data.util.METADATA_KEY_ALBUM_ID
import com.techdroidcentre.data.util.METADATA_KEY_ARTIST_ID
import com.techdroidcentre.data.util.METADATA_KEY_FLAG
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val BROWSABLE_ROOT = "/"
const val ALBUMS_ROOT = "ALBUMS"
const val ARTISTS_ROOT = "ARTISTS"
const val SONGS_ROOT = "SONGS"
const val PLAYLISTS_ROOT = "PLAYLISTS"
const val RESOURCE_ROOT_URI = "android.resource://com.techdroidcentre.musicplayer/drawable/"

class BrowseRoot @Inject constructor(
    @ApplicationContext context: Context,
    musicSource: MusicSource
) {
    val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

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

        val playlistsMetadata = MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_MEDIA_ID, PLAYLISTS_ROOT)
            putString(METADATA_KEY_TITLE, context.getString(R.string.playlists_title))
            putString(METADATA_KEY_ALBUM_ART_URI, RESOURCE_ROOT_URI + context.resources.getResourceEntryName(R.drawable.baseline_queue_music_24))
            putLong(METADATA_KEY_FLAG, FLAG_BROWSABLE.toLong())
        }.build()

        rootList += albumsMetadata
        rootList += artistsMetadata
        rootList += songsMetadata
        rootList += playlistsMetadata

        mediaIdToChildren[BROWSABLE_ROOT] = rootList

        musicSource.songs.forEach { mediaMetadata ->
            val songsChildren = mediaIdToChildren[SONGS_ROOT] ?: mutableListOf()
            songsChildren += mediaMetadata
            mediaIdToChildren[SONGS_ROOT] = songsChildren

            val albumId = mediaMetadata.getLong(METADATA_KEY_ALBUM_ID)
            val albumChildren = mediaIdToChildren[albumId.toString()] ?: buildAlbumRoot(mediaMetadata)
            albumChildren += mediaMetadata

            val artistId = mediaMetadata.getLong(METADATA_KEY_ARTIST_ID)
            val artistChildren = mediaIdToChildren[artistId.toString()] ?: buildArtistRoot(mediaMetadata)
            artistChildren += mediaMetadata
        }
    }

    private fun buildAlbumRoot(mediaMetadata: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val albumId = mediaMetadata.getLong(METADATA_KEY_ALBUM_ID)
        val artUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            // use this album item uri to load album art on android Q and later
            ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
        else ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_MEDIA_ID, albumId.toString())
            putString(METADATA_KEY_TITLE, mediaMetadata.getString(METADATA_KEY_ALBUM))
            putString(METADATA_KEY_ARTIST, mediaMetadata.getString(METADATA_KEY_ARTIST))
            putString(METADATA_KEY_ART_URI, artUri.toString())
            putLong(METADATA_KEY_FLAG, FLAG_BROWSABLE.toLong())
        }.build()

        val albumsRoot = mediaIdToChildren[ALBUMS_ROOT] ?: mutableListOf()
        albumsRoot += albumMetadata
        mediaIdToChildren[ALBUMS_ROOT] = albumsRoot

        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[albumId.toString()] = it
        }
    }

    private fun buildArtistRoot(mediaMetadata: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val artistId = mediaMetadata.getLong(METADATA_KEY_ARTIST_ID)
        val artistMetadata = MediaMetadataCompat.Builder().apply {
            putString(METADATA_KEY_MEDIA_ID, artistId.toString())
            putString(METADATA_KEY_TITLE, mediaMetadata.getString(METADATA_KEY_ARTIST))
            putString(METADATA_KEY_ARTIST, mediaMetadata.getString(METADATA_KEY_ARTIST))
            // use this media's art as artist cover-art
            putBitmap(METADATA_KEY_ART, mediaMetadata.getBitmap(METADATA_KEY_ART))
            putLong(METADATA_KEY_FLAG, FLAG_BROWSABLE.toLong())
        }.build()

        val artistsRoot = mediaIdToChildren[ARTISTS_ROOT] ?: mutableListOf()
        artistsRoot += artistMetadata
        mediaIdToChildren[ARTISTS_ROOT] = artistsRoot

        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[artistId.toString()] = it
        }
    }

    fun deleteSong(parentId: String, mediaUri: String) {
        val albumId = getAlbumIdForSong(parentId, mediaUri)
        val artistId = getArtistIdForSong(parentId, mediaUri)
        val songs = mediaIdToChildren[parentId] ?: mutableListOf()
        val albumSongs = mediaIdToChildren[albumId] ?: mutableListOf()
        val artistSongs = mediaIdToChildren[artistId] ?: mutableListOf()
        val song = songs.first { it.getString(METADATA_KEY_MEDIA_URI) == mediaUri }

        songs -= song
        albumSongs -= song
        artistSongs -= song

        // Remove albums with empty songs
        val albums = mediaIdToChildren[ALBUMS_ROOT]!!
        val emptyAlbums = albums.filter { album ->
            mediaIdToChildren[album.getString(METADATA_KEY_MEDIA_ID)]?.isEmpty() ?: false
        }
        mediaIdToChildren[ALBUMS_ROOT]?.removeAll(emptyAlbums)

        // Remove artists with zero songs
        val artists = mediaIdToChildren[ARTISTS_ROOT]!!
        val emptyArtists = artists.filter { artist ->
            mediaIdToChildren[artist.getString(METADATA_KEY_MEDIA_ID)]?.isEmpty() ?: false
        }
        mediaIdToChildren[ARTISTS_ROOT]?.removeAll(emptyArtists)
    }

    fun getAlbumIdForSong(parentId: String, mediaUri: String): String {
        val children = mediaIdToChildren[parentId] ?: mutableListOf()
        val mediaMetadata = children.first { it.getString(METADATA_KEY_MEDIA_URI) == mediaUri }
        return mediaMetadata.getLong(METADATA_KEY_ALBUM_ID).toString()
    }

    fun getArtistIdForSong(parentId: String, mediaUri: String): String {
        val children = mediaIdToChildren[parentId] ?: mutableListOf()
        val mediaMetadata = children.first { it.getString(METADATA_KEY_MEDIA_URI) == mediaUri }
        return mediaMetadata.getLong(METADATA_KEY_ARTIST_ID).toString()
    }

}