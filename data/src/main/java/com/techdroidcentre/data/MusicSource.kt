package com.techdroidcentre.data

import android.content.Context
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.techdroidcentre.data.State.*
import com.techdroidcentre.data.mapper.toAlbum
import com.techdroidcentre.data.mapper.toArtist
import com.techdroidcentre.data.mapper.toMediaMetadataCompat
import com.techdroidcentre.data.mapper.toSong
import com.techdroidcentre.data.queries.AlbumQuery
import com.techdroidcentre.data.queries.ArtistQuery
import com.techdroidcentre.data.queries.SongsQuery
import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicSource @Inject constructor(
    private val songsQuery: SongsQuery,
    private val albumQuery: AlbumQuery,
    private val artistQuery: ArtistQuery,
    @ApplicationContext private val context: Context
) {
    var songs = emptyList<MediaMetadataCompat>()
    var albums = emptyList<MediaMetadataCompat>()
    var artists = emptyList<MediaMetadataCompat>()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALISED || state == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALISED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALISING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALISED)
            true
        }
    }

    suspend fun fetchSongs() = withContext(Dispatchers.IO) {
        state = STATE_INITIALISING
        val allSongs = getAllSongs()
        songs = allSongs.map { song ->
            song.toMediaMetadataCompat()
        }
        state = STATE_INITIALISED
    }

    suspend fun fetchAlbums() = withContext(Dispatchers.IO) {
        state = STATE_INITIALISING
        albums = getAllAlbums().map { album ->
            album.toMediaMetadataCompat()
        }
        state = STATE_INITIALISED
    }

    suspend fun fetchArtists() = withContext(Dispatchers.IO) {
        state = STATE_INITIALISING
        albums = getAllArtists().map { artist ->
            artist.toMediaMetadataCompat()
        }
        state = STATE_INITIALISED
    }

    private fun getAllSongs(): List<Song> {
        val songs = mutableListOf<Song>()
        val query = songsQuery.getSongsCursor()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                val song = cursor.toSong(context, idColumn, artistIdColumn, titleColumn, artistColumn,
                    albumIdColumn, albumColumn, pathColumn, durationColumn, sizeColumn
                )
                songs.add(song)
            }
        }
        return songs
    }

    private fun getAllAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        albumQuery.getAlbumsCursor()?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
            val numOfSongsColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (cursor.moveToNext()) {
                val album = cursor.toAlbum(context, idColumn, albumColumn, artistColumn, numOfSongsColumn)
                albums.add(album)
            }
        }
        return albums
    }

    private fun getAllArtists(): List<Artist> {
        val artists = mutableListOf<Artist>()
        artistQuery.getArtistsCursor()?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
            val numOfTracksColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            while (cursor.moveToNext()) {
               val artist = cursor.toArtist(context, idColumn, artistColumn, numOfTracksColumn)
                artists.add(artist)
            }
        }
        return artists
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALISING,
    STATE_INITIALISED,
    STATE_ERROR
}