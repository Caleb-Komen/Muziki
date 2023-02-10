package com.techdroidcentre.player

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.techdroidcentre.data.MusicSource
import javax.inject.Inject

class MusicPlaybackPreparer @Inject constructor(
    private val musicSource: MusicSource,
    private val deleteSong: (String, String) -> Unit,
    private val preparePlaylist: (MediaMetadataCompat, Boolean, String?, List<MediaMetadataCompat>) -> Unit
): MediaSessionConnector.PlaybackPreparer {
    override fun onCommand(
        player: Player,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean {
        when(command) {
            COMMAND -> {
                val parentId = extras?.getString(KEY_PARENT_ID).toString()
                val mediaUri = extras?.getString(KEY_MEDIA_URI).toString()
                deleteSong(parentId, mediaUri)
            }
        }
        return false
    }

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepare(playWhenReady: Boolean) = Unit

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        musicSource.whenReady {
            val itemToPlay = musicSource.songs.find { it.getString(METADATA_KEY_MEDIA_ID) == mediaId }
            val parentId = extras?.getString(EXTRA_PARENT_ID)
            val songIds = extras?.getStringArrayList(EXTRA_SONGS_IDS)
            val playlistSongs = musicSource.songs.filter {
                songIds?.contains(it.getString(METADATA_KEY_MEDIA_ID)) ?: false
            }
            if (itemToPlay == null){
                Log.d(TAG, "Media with id $mediaId not found.")
            } else{
                if (songIds == null) {
                    preparePlaylist(itemToPlay, playWhenReady, parentId, emptyList())
                } else {
                    preparePlaylist(itemToPlay, playWhenReady, parentId, playlistSongs)
                }
            }
        }
    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
}

private const val TAG = "MusicPlaybackPreparer"
const val EXTRA_PARENT_ID = "com.techdroidcentre.player.EXTRA_PARENT_ID"
const val EXTRA_SONGS_IDS = "com.techdroidcentre.player.EXTRA_SONGS_IDS"
const val COMMAND = "DELETE_SONG"
const val KEY_PARENT_ID = "com.techdroidcentre.player.KEY_PARENT_ID"
const val KEY_MEDIA_URI = "com.techdroidcentre.player.KEY_MEDIA_URI"