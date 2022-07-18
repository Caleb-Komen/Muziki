package com.techdroidcentre.data.queries

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

class ArtistQuery(
    private val context: Context
) {
    fun getArtistsCursor(): Cursor? {
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )
        val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"
        return context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
    }

}