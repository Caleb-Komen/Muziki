package com.techdroidcentre.musicplayer.model

import android.graphics.Bitmap

data class ArtistData(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val browsable: Boolean = false,
    val coverArt: Bitmap? = null
)
