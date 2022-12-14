package com.techdroidcentre.musicplayer.ui.nowplaying

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.techdroidcentre.musicplayer.util.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _playbackState = musicServiceConnection.playbackState
    val playbackState: LiveData<PlaybackStateCompat> = _playbackState

    private val _metadata = musicServiceConnection.mediaMetadata
    val metadata: LiveData<MediaMetadataCompat> = _metadata

    private val transportControls get() = musicServiceConnection.transportControls

    fun playOrPause() {
        val isPlaying = _playbackState.value?.state == PlaybackStateCompat.STATE_PLAYING
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
}
