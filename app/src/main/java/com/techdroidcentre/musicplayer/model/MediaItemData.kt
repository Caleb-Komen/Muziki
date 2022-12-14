package com.techdroidcentre.musicplayer.model

data class MediaItemData(
    val mediaId: String="",
    val title: String="",
    val subtitle: String="",
    val description: String="",
    val browsable: Boolean=false,
    val coverArt: String = ""
)
