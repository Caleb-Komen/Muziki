package com.techdroidcentre.data.queries

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ArtistQuery @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getArtistsCursor(): Cursor? {
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        return context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
        )
    }

}