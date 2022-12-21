package com.techdroidcentre.data.mapper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.util.Size
import androidx.annotation.RequiresApi
import com.techdroidcentre.data.util.*
import com.techdroidcentre.domain.models.Song

fun Song.toMediaMetadataCompat(context: Context): MediaMetadataCompat {
    val coverArt = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getThumbnail(context, coverArt)
        } else {
            getCoverArt(path)
        }
    } catch (ex: Exception) {
        null
    }

    return MediaMetadataCompat.Builder().apply {
        putString(METADATA_KEY_MEDIA_ID, id.toString())
        putString(METADATA_KEY_TITLE, title)
        putLong(METADATA_KEY_ARTIST_ID, artistId)
        putString(METADATA_KEY_ARTIST, artist)
        putLong(METADATA_KEY_ALBUM_ID, albumId)
        putString(METADATA_KEY_ALBUM, album)
        putString(METADATA_KEY_DISPLAY_TITLE, title)
        putString(METADATA_KEY_DISPLAY_SUBTITLE, artist)
        putString(METADATA_KEY_DISPLAY_DESCRIPTION, album)
        putString(METADATA_KEY_MEDIA_URI, uri)
        putString(METADATA_KEY_PATH, path)
        putLong(METADATA_KEY_DURATION, duration)
        putLong(METADATA_KEY_SIZE, size.toLong())
        putBitmap(METADATA_KEY_ART, coverArt)
        putLong(METADATA_KEY_FLAG, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE.toLong())
    }.build()
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getThumbnail(context: Context, coverArt: String): Bitmap? {
    return context.contentResolver.loadThumbnail(
        Uri.parse(coverArt),
        Size(300, 300),
        null
    )
}

fun getCoverArt(path: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    val art = retriever.embeddedPicture ?: return null
    retriever.release()
    return BitmapFactory.decodeByteArray(art, 0, art.size)
}