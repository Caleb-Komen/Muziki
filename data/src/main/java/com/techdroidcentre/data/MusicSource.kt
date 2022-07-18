package com.techdroidcentre.data

import android.provider.MediaStore
import com.techdroidcentre.data.mapper.toAlbum
import com.techdroidcentre.data.mapper.toArtist
import com.techdroidcentre.data.mapper.toSong
import com.techdroidcentre.data.queries.AlbumQuery
import com.techdroidcentre.data.queries.ArtistQuery
import com.techdroidcentre.data.queries.SongsQuery
import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song

class MusicSource(
    private val songsQuery: SongsQuery,
    private val albumQuery: AlbumQuery,
    private val artistQuery: ArtistQuery
) {
    fun getAllSongs(): List<Song> {
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
                val song = cursor.toSong(idColumn, artistIdColumn, titleColumn, artistColumn,
                    albumIdColumn, albumColumn, pathColumn, durationColumn, sizeColumn
                )
                songs.add(song)
            }
        }
        return songs
    }

    fun getAlbums(): List<Album> {
        val albums = mutableListOf<Album>()
        albumQuery.getAlbumsCursor()?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Albums._ID)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)
            val numOfSongsColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)

            while (cursor.moveToNext()) {
                val album = cursor.toAlbum(idColumn, albumColumn, artistColumn, numOfSongsColumn)
                albums.add(album)
            }
        }
        return albums
    }

    fun getArtists(): List<Artist> {
        val artists = mutableListOf<Artist>()
        artistQuery.getArtistsCursor()?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)
            val numOfTracksColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            while (cursor.moveToNext()) {
               val artist = cursor.toArtist(idColumn, artistColumn, numOfTracksColumn)
                artists.add(artist)
            }
        }
        return artists
    }
}