package com.techdroidcentre.musicplayer.model

data class NowPlayingMetadata(
    val id: String,
    val title: String,
    val subtitle: String,
    val duration: Long,
    val albumArt: String
)
