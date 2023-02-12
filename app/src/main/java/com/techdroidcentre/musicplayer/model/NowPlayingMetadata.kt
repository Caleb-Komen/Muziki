package com.techdroidcentre.musicplayer.model

import android.graphics.Bitmap

data class NowPlayingMetadata(
    val id: String,
    val title: String,
    val subtitle: String,
    val duration: Long,
    val albumArt: Bitmap?
)
