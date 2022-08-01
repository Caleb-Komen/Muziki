package com.techdroidcentre.data.queries

import com.techdroidcentre.domain.models.Album
import com.techdroidcentre.domain.models.Artist
import com.techdroidcentre.domain.models.Song

class FakeMediaQueryImpl: MediaQuery {
    private var isNull = false

    fun setNull() {
        isNull = true
    }

    override fun getAllSongs(): List<Song>? {
        if (isNull) return null

        return listOf(
            Song(1L, "content://1", "Honeycomb", 100L, "Jimmie Rodgers", 10L, "Greatest Hits Of The 50's", "emulated/0/", 120000L, 2),
            Song(2L, "content://2", "What's broken", 101L, "David Crosby", 11L, "Unknown", "emulated/0/", 120000L, 2),
            Song(3L, "content://3", "Friends", 102L, "Unknown", 12L, "Unknown", "emulated/0/", 120000L, 2),
        )
    }

    override fun getAllAlbums(): List<Album>? {
        if (isNull) return null

        return listOf(
            Album(10L, "content://4", "Greatest Hits Of The 50's", "Jimmie Rodgers", 1),
            Album(11L, "content://5", "Unknown", "David Crosby", 1),
            Album(12L, "content://6", "Unknown", "Unknown", 1)
        )
    }

    override fun getAllArtists(): List<Artist>? {
        if (isNull) return null

        return listOf(
            Artist(100L, "content://7", "Jimmie Rodgers", 1),
            Artist(101L, "content://8", "David Crosby", 1),
            Artist(102L, "content://9", "Unknown", 1),
        )
    }
}