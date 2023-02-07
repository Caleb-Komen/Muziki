package com.techdroidcentre.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi

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