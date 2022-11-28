package com.techdroidcentre.musicplayer.ui.songs

import android.support.v4.media.MediaBrowserCompat
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
class SongsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _songs = MutableLiveData<List<MediaItemData>>()
    val songs: LiveData<List<MediaItemData>> = _songs

    private val _mediaId = savedStateHandle.getLiveData<String>(MEDIA_ID_KEY)
    val mediaId: LiveData<String> = _mediaId

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val songItems = children.map {
                MediaItemData(
                    mediaId = it.mediaId!!,
                    title = it.description.title.toString(),
                    subtitle = it.description.subtitle.toString(),
                    description = it.description.description.toString(),
                    browsable = it.isBrowsable,
                    coverArt = it.description.iconUri.toString()
                )
            }
            _songs.value = songItems
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