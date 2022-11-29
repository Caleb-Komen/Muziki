package com.techdroidcentre.musicplayer.util

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context,
    component: ComponentName
) {
    val isConnected = MutableLiveData<Boolean>().apply {
        postValue(false)
    }

    private lateinit var mediaController: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    val playbackState =  MutableLiveData<PlaybackStateCompat>()

    val mediaBrowser = MediaBrowserCompat(
        context,
        component,
        MediaBrowserConnectionCallback(context),
        null
    ).apply { connect() }

    val rootMediaId: String get() = mediaBrowser.root

    fun subscribe(parentId: String, subscriptionCallback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, subscriptionCallback)
    }

    fun unsubscribe(parentId: String, subscriptionCallback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, subscriptionCallback)
    }

    inner class MediaBrowserConnectionCallback(private val context: Context) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken)
            mediaController.registerCallback(mediaControllerCallback)
            isConnected.postValue(true)
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            isConnected.postValue(false)
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            isConnected.postValue(false)
        }
    }

    val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            playbackState.value = state
        }
    }
}