package com.techdroidcentre.data.queries

import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song

interface MediaQuery {
    fun getAllSongs(): List<Song>

    fun getAllAlbums(): List<Album>

    fun getAllArtists(): List<Artist>
}