package com.techdroidcentre.musicplayer.ui.home

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.techdroidcentre.musicplayer.model.MediaItemData
import com.techdroidcentre.musicplayer.util.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    val rootId: LiveData<String> = Transformations.map(musicServiceConnection.isConnected) { isConnected ->
        if (isConnected) musicServiceConnection.rootMediaId else null
    }

    private val _mediaItems = MutableLiveData<List<MediaItemData>>()
    val mediaItems: LiveData<List<MediaItemData>> = _mediaItems

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val items = children.map { child ->
                MediaItemData(
                    child.mediaId!!,
                    child.description.title.toString(),
                    child.description.subtitle.toString(),
                    child.description.description.toString(),
                    child.isBrowsable,
                    child.description.iconUri.toString()
                )
            }

            _mediaItems.value = items
        }
    }

    fun subscribe(mediaId: String) {
        musicServiceConnection.also {
            it.subscribe(mediaId, subscriptionCallback)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(rootId.value!!, subscriptionCallback)
    }
}