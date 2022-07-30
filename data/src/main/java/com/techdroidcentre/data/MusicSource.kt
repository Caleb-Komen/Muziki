package com.techdroidcentre.data

import android.support.v4.media.MediaMetadataCompat
import com.techdroidcentre.data.State.*
import com.techdroidcentre.data.mapper.toMediaMetadataCompat
import com.techdroidcentre.data.queries.MediaQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicSource @Inject constructor(
    private val mediaQuery: MediaQuery,
    private val dispatcher: CoroutineDispatcher
) {
    var songs = emptyList<MediaMetadataCompat>()
    var albums = emptyList<MediaMetadataCompat>()
    var artists = emptyList<MediaMetadataCompat>()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALISED || state == STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == STATE_INITIALISED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALISING) {
            onReadyListeners += action
            false
        } else {
            action(state == STATE_INITIALISED)
            true
        }
    }

    suspend fun fetchSongs() = withContext(dispatcher) {
        state = STATE_INITIALISING
        val allSongs = mediaQuery.getAllSongs()
        songs = allSongs.map { song ->
            song.toMediaMetadataCompat()
        }
        state = STATE_INITIALISED
    }

    suspend fun fetchAlbums() = withContext(dispatcher) {
        state = STATE_INITIALISING
        albums = mediaQuery.getAllAlbums().map { album ->
            album.toMediaMetadataCompat()
        }
        state = STATE_INITIALISED
    }

    suspend fun fetchArtists() = withContext(dispatcher) {
        state = STATE_INITIALISING
        albums = mediaQuery.getAllArtists().map { artist ->
            artist.toMediaMetadataCompat()
        }
        state = STATE_INITIALISED
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALISING,
    STATE_INITIALISED,
    STATE_ERROR
}