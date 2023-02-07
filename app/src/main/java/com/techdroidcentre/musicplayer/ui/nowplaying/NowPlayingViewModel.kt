package com.techdroidcentre.musicplayer.ui.nowplaying

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.techdroidcentre.musicplayer.model.NowPlayingMetadata
import com.techdroidcentre.musicplayer.util.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    val mediaMetadata = MutableLiveData(
        NowPlayingMetadata(
            id = "",
            title = "Not Playing",
            subtitle = "",
            duration = 0L,
            albumArt = ""
        )
    )
    val isPlaying = MutableLiveData(false)
    private var updatePosition = true

    val mediaPosition = MutableLiveData(0L)

    private val transportControls get() = musicServiceConnection.transportControls

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = musicServiceConnection.mediaMetadata.value ?: NOTHING_PLAYING
        updateState(playbackState, metadata)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
    }
    val serviceConnection = musicServiceConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.mediaMetadata.observeForever(mediaMetadataObserver)
        checkMediaPosition()
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)
        musicServiceConnection.mediaMetadata.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        val isPlaying = playbackState.state == PlaybackStateCompat.STATE_PLAYING
        val id = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        val duration = mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

        if (duration > 0L && id != null) {
            val nowPlayingMetadata = NowPlayingMetadata(
                id = id,
                title = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "Unknown",
                subtitle = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)
                    ?: "Unknown",
                duration = duration,
                albumArt = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)
            )
            this.mediaMetadata.postValue(nowPlayingMetadata)
        }
        this.isPlaying.postValue(isPlaying)
    }

    private fun checkMediaPosition() {
        Handler(Looper.getMainLooper()).postDelayed({
            val currentPosition = if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                val timeDelta = SystemClock.elapsedRealtime() - playbackState.lastPositionUpdateTime
                (playbackState.position + (timeDelta * playbackState.playbackSpeed)).toLong()
            } else {
                playbackState.position
            }
            if (mediaPosition.value != currentPosition) {
                mediaPosition.postValue(currentPosition)
            }
            if(updatePosition) {
                checkMediaPosition()
            }
        }, 100L)
    }

    fun playOrPause() {
        val isPlaying = playbackState.state == PlaybackStateCompat.STATE_PLAYING
        when {
            isPlaying -> transportControls.pause()
            else -> transportControls.play()
        }
    }

    fun skipToNext() {
        transportControls.skipToNext()
    }

    fun skipToPrevious() {
        transportControls.skipToPrevious()
    }

    fun seekTo(position: Float) {
        transportControls.seekTo(position.toLong())
    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()
