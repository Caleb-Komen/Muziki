package com.techdroidcentre.data.mapper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song
import java.io.IOException

fun Cursor.toSong(
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
    val artist = getString(artistColumn)
    val album = getString(albumColumn)
    val path = getString(pathColumn)
    val duration = getLong(durationColumn)
    val size = getInt(sizeColumn)
    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        id
    ).toString()

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

fun Cursor.toArtist(idColumn: Int, artistColumn: Int, numOfTracksColumn: Int): Artist {
    val id = getLong(idColumn)
    val name = getString(artistColumn)
    val numOfSongs = getInt(numOfTracksColumn)
    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        id
    ).toString()

    return Artist(
        id = id,
        uri = uri,
        name = name,
        numOfSongs = numOfSongs
    )
}

fun Cursor.toAlbum(idColumn: Int, albumColumn: Int, artistColumn: Int, numOfSongsColumn: Int): Album {
    val id = getLong(idColumn)
    val albumName = getString(albumColumn)
    val artist = getString(artistColumn)
    val numOfSongs = getInt(numOfSongsColumn)
    val uri = ContentUris.withAppendedId(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        id
    ).toString()

    return Album(
        id = id,
        uri = uri,
        name = albumName,
        artist = artist,
        numOfSongs = numOfSongs
    )
}

