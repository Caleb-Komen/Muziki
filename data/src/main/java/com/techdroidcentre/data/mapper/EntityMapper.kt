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
import com.techdroidcentre.data.db.entity.PlayListEntity
import com.techdroidcentre.data.db.entity.PlayListSongEntity
import com.techdroidcentre.data.model.PlayList
import com.techdroidcentre.data.model.PlayListSong
import com.techdroidcentre.data.util.*
import com.techdroidcentre.domain.models.Song

fun Song.toMediaMetadataCompat(context: Context): MediaMetadataCompat {
    val coverArt = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getThumbnail(context, coverArt)
        } else {
            getCoverArt(coverArt)
        }
    } catch(ex: Exception) {
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

fun PlayListSong.toEntity(playListId: Long): PlayListSongEntity {
    return PlayListSongEntity(
        id = id,
        mediaUri = mediaUri,
        title = title,
        album = album,
        artist = artist,
        artUri = artUri,
        playListId = playListId
    )
}

fun PlayListSongEntity.toModel(): PlayListSong {
    return PlayListSong(
        id = id,
        mediaUri = mediaUri,
        title = title,
        album = album,
        artist = artist,
        artUri = artUri
    )
}

fun PlayList.toEntity(): PlayListEntity {
    return PlayListEntity(id = id, name = name)
}

fun PlayListEntity.toModel(): PlayList {
    return PlayList(id = id, name = name)
}