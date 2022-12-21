package com.techdroidcentre.domain.models

data class Song(
    val id: Long,
    val uri: String,
    val title: String,
    val artistId: Long,
    val artist: String,
    val albumId: Long,
    val album: String,
    val path: String,
    val duration: Long,
    val size: Int,
    val coverArt: String
)
