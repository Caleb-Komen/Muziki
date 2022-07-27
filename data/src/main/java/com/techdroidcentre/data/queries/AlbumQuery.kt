package com.techdroidcentre.data.queries

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlbumQuery @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getAlbumsCursor(): Cursor? {
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.ARTIST
        )

        return context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        )
    }
}