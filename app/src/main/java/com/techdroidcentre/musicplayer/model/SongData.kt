package com.techdroidcentre.musicplayer.model

data class SongData(
    val mediaId: String = "",
    val uri: String = "",
    val title: String = "",
    val subtitle: String = "",
    val description: String = "",
    val browsable: Boolean = false,
    val coverArt: String = ""
)
