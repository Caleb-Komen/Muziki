package com.techdroidcentre.data.queries

import com.techdroidcentre.domain.models.Song

class FakeMediaQueryImpl: MediaQuery {
    private var isNull = false

    fun setNull() {
        isNull = true
    }

    override fun getAllSongs(): List<Song>? {
        if (isNull) return null

        return listOf(
            Song(1L, "content://1", "Honeycomb", 100L, "Jimmie Rodgers", 10L, "Greatest Hits Of The 50's", "emulated/0/", 120000L, 2, ""),
            Song(2L, "content://2", "What's broken", 101L, "David Crosby", 11L, "Unknown", "emulated/0/", 120000L, 2, ""),
            Song(3L, "content://3", "Friends", 102L, "Unknown", 12L, "Unknown", "emulated/0/", 120000L, 2, ""),
        )
    }
}