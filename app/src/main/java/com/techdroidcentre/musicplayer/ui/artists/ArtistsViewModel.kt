package com.techdroidcentre.musicplayer.ui.artists

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.techdroidcentre.musicplayer.model.ArtistData
import com.techdroidcentre.musicplayer.ui.MEDIA_ID_KEY
import com.techdroidcentre.musicplayer.util.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _artists = MutableLiveData<List<ArtistData>>()
    val artists: LiveData<List<ArtistData>> = _artists

    private val _mediaId = savedStateHandle.getLiveData<String>(MEDIA_ID_KEY)
    val mediaId: LiveData<String> = _mediaId

    private val subscriptionCallback = object : SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val artistItems = children.map {
                ArtistData(
                    mediaId = it.mediaId!!,
                    title = it.description.title.toString(),
                    subtitle = it.description.subtitle.toString(),
                    description = it.description.description.toString(),
                    browsable = it.isBrowsable,
                    coverArt = it.description.iconBitmap
                )
            }
            _artists.value = artistItems
        }
    }

    fun subscribe(mediaId: String) {
        musicServiceConnection.subscribe(mediaId, subscriptionCallback)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(_mediaId.value!!, subscriptionCallback)
    }
}