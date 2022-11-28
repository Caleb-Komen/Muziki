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

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALISED || value == STATE_ERROR) {
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
        allSongs?.let { it ->
            songs = it.map { song ->
                song.toMediaMetadataCompat()
            }
            state = STATE_INITIALISED
        } ?: run {
            songs = emptyList()
            state = STATE_ERROR
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALISING,
    STATE_INITIALISED,
    STATE_ERROR
}