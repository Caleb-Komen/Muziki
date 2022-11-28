package com.techdroidcentre.data.queries

import com.techdroidcentre.domain.models.Song

interface MediaQuery {
    fun getAllSongs(): List<Song>?
}