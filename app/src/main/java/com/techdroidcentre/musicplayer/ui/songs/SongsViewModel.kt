package com.techdroidcentre.musicplayer.ui.songs

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import com.techdroidcentre.data.SONGS_ROOT
import com.techdroidcentre.data.repository.PlayListRepository
import com.techdroidcentre.data.repository.PlayListSongRepository
import com.techdroidcentre.musicplayer.mapper.toModel
import com.techdroidcentre.musicplayer.mapper.toPlaylistSong
import com.techdroidcentre.musicplayer.mapper.toSongData
import com.techdroidcentre.musicplayer.mapper.toViewState
import com.techdroidcentre.musicplayer.model.PlayListViewState
import com.techdroidcentre.musicplayer.model.SongData
import com.techdroidcentre.musicplayer.ui.MEDIA_ID_KEY
import com.techdroidcentre.musicplayer.ui.PLAYLIST_ID_KEY
import com.techdroidcentre.musicplayer.util.MusicServiceConnection
import com.techdroidcentre.player.COMMAND
import com.techdroidcentre.player.EXTRA_PARENT_ID
import com.techdroidcentre.player.KEY_MEDIA_URI
import com.techdroidcentre.player.KEY_PARENT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val playlistRepository: PlayListRepository,
    private val playlistSongRepository: PlayListSongRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _songs = MutableLiveData<List<SongData>>()
    val songs: LiveData<List<SongData>> = _songs

    private val _mediaId = savedStateHandle.getLiveData<String>(MEDIA_ID_KEY)
    val mediaId: LiveData<String> = _mediaId

    private val playlistId = savedStateHandle.getLiveData<Long>(PLAYLIST_ID_KEY)

    private val _playlistSongs = Transformations.switchMap(playlistId) { id ->
        if (id != null) {
            Transformations.map(playlistSongRepository.getPlayListSongs(id)) { songs ->
                songs.map { it.toSongData() }
            }
        } else {
            MutableLiveData(emptyList())
        }
    }
    val playlistSongs: LiveData<List<SongData>> = _playlistSongs

    private val _playlists = Transformations.map(playlistRepository.getPlayLists()) { playlists ->
        playlists.map { it.toViewState() }
    }
    val playlists: LiveData<List<PlayListViewState>> = _playlists

    private val selectedSongs = mutableListOf<SongData>()

    private val transportControls get() = musicServiceConnection.transportControls

    var nowPlayingMediaId: String? = null

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            val songItems = children.map {
                SongData(
                    mediaId = it.mediaId!!,
                    uri = it.description.mediaUri.toString(),
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

    fun playSong(mediaId: String) {
        val playbackState = musicServiceConnection.playbackState.value
        val isPlaying = playbackState?.state == PlaybackStateCompat.STATE_PLAYING
        val isPaused = playbackState?.state == PlaybackStateCompat.STATE_PAUSED
        if ((isPlaying || isPaused) && (mediaId == nowPlayingMediaId)) {
            when {
                isPlaying -> transportControls.pause()
                isPaused -> transportControls.play()
            }
        } else {
            val extras = Bundle()
            extras.putString(EXTRA_PARENT_ID, _mediaId.value)
            transportControls.playFromMediaId(mediaId, extras)
        }
        nowPlayingMediaId = mediaId
    }

    fun addToSelectedSongs(song: SongData) {
        if (!selectedSongs.contains(song)) selectedSongs.add(song)
    }

    fun removeFromSelectedSongs(song: SongData) {
        if (selectedSongs.contains(song)) selectedSongs.remove(song)
    }

    fun clearSelectedSongs() {
        selectedSongs.clear()
    }

    fun addSongs() {
        val playlistId = playlistId.value ?: return
        if (selectedSongs.isNotEmpty()) {
            viewModelScope.launch {
                selectedSongs.forEach {
                    playlistSongRepository.addSong(playlistId, it.toPlaylistSong())
                }
            }
        }
    }

    fun deleteSong(uri: String) {
        val args = Bundle()
        args.putString(KEY_PARENT_ID, SONGS_ROOT)
        args.putString(KEY_MEDIA_URI, uri)
        musicServiceConnection.sendCommand(COMMAND, args)
    }

    fun deletePlaylistSong(id: String) {
        viewModelScope.launch {
            playlistSongRepository.deleteSong(id)
        }
    }

    fun addSongToPlaylist(playlistId: Long, song: SongData) {
        viewModelScope.launch {
            playlistSongRepository.addSong(playlistId, song.toPlaylistSong())
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(PlayListViewState(0L, name).toModel())
        }
    }
}