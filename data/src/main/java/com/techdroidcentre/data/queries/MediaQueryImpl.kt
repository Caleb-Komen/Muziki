package com.techdroidcentre.data.queries

import android.content.Context
import android.provider.MediaStore
import com.techdroidcentre.data.mapper.toSong
import com.techdroidcentre.domain.models.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaQueryImpl @Inject constructor(
    private val songsQuery: SongsQuery,
    @ApplicationContext private val context: Context
): MediaQuery {
    override fun getAllSongs(): List<Song>? {
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
            return songs
        } ?: run {
            return null
        }
    }
}