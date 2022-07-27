package com.techdroidcentre.data.mapper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.techdroidcentre.data.R
import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song

fun Cursor.toSong(
    context: Context,
    idColumn: Int,
    artistIdColumn: Int,
    titleColumn: Int,
    artistColumn: Int,
    albumIdColumn: Int,
    albumColumn: Int,
    pathColumn: Int,
    durationColumn: Int,
    sizeColumn: Int
): Song {
    val id = getLong(idColumn)
    val artistId = getLong(artistIdColumn)
    val albumId = getLong(albumIdColumn)
    val title = getString(titleColumn)
    val artistName = getString(artistColumn)
    val albumName = getString(albumColumn)
    val path = getString(pathColumn)
    val duration = getLong(durationColumn)
    val size = getInt(sizeColumn)
    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    ).toString()

    val album = if (albumName == MediaStore.UNKNOWN_STRING) context.getString(R.string.unknown_album_title) else albumName
    val artist = if (artistName == MediaStore.UNKNOWN_STRING) context.getString(R.string.unknown_artist_title) else artistName

    return Song(
        id = id,
        uri = uri,
        title = title,
        artistId = artistId,
        artist = artist,
        albumId = albumId,
        album = album,
        path = path,
        duration = duration,
        size = size
    )
}

fun Cursor.toArtist(context: Context, idColumn: Int, artistColumn: Int, numOfTracksColumn: Int): Artist {
    val id = getLong(idColumn)
    val artist = getString(artistColumn)
    val numOfSongs = getInt(numOfTracksColumn)
    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        id
    ).toString()

    val name = if (artist == MediaStore.UNKNOWN_STRING) context.getString(R.string.unknown_artist_title) else artist

    return Artist(
        id = id,
        uri = uri,
        name = name,
        numOfSongs = numOfSongs
    )
}

fun Cursor.toAlbum(context: Context, idColumn: Int, albumColumn: Int, artistColumn: Int, numOfSongsColumn: Int): Album {
    val id = getLong(idColumn)
    val albumName = getString(albumColumn)
    val artist = getString(artistColumn)
    val numOfSongs = getInt(numOfSongsColumn)
    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        id
    ).toString()

    val name = if (albumName == MediaStore.UNKNOWN_STRING) context.getString(R.string.unknown_album_title) else albumName

    return Album(
        id = id,
        uri = uri,
        name = name,
        artist = artist,
        numOfSongs = numOfSongs
    )
}

