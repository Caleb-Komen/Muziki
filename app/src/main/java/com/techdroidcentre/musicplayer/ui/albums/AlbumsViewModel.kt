package com.techdroidcentre.musicplayer.ui.albums

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.techdroidcentre.musicplayer.model.MediaItemData
import com.techdroidcentre.musicplayer.ui.MEDIA_ID_KEY
import com.techdroidcentre.musicplayer.util.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _albums = MutableLiveData<List<MediaItemData>>()
    val albums: LiveData<List<MediaItemData>> = _albums

    private val _mediaId = savedStateHandle.getLiveData<String>(MEDIA_ID_KEY)
    val mediaId: LiveData<String> = _mediaId

    private val subscriptionCallback = object : SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val albumItems = children.map {
                MediaItemData(
                    it.mediaId!!,
                    it.description.title.toString(),
                    it.description.subtitle.toString(),
                    it.description.description.toString(),
                    it.isBrowsable,
                    it.description.iconUri.toString()
                )
            }
            _albums.value = albumItems
        }
    }

    fun subscribe(mediaId: String) {
        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId.value!!, subscriptionCallback)
    }
}