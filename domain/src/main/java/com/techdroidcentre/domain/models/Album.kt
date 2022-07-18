package com.techdroidcentre.domain.models

data class Album(
    val id: Long,
    val uri: String,
    val name: String,
    val artist: String,
    val numOfSongs: Int
)
