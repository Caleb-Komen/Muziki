package com.techdroidcentre.player

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator

class MusicQueueNavigator(
    mediaSession: MediaSessionCompat,
    private val currentPlaylistItems: List<MediaMetadataCompat>
) : TimelineQueueNavigator(mediaSession) {
    override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
        return if (windowIndex < currentPlaylistItems.size) {
            currentPlaylistItems[windowIndex].description
        } else {
            MediaDescriptionCompat.Builder().build()
        }
    }
}