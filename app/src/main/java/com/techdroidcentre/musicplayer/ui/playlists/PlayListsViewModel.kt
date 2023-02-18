package com.techdroidcentre.musicplayer.ui.playlists

import androidx.lifecycle.*
import com.techdroidcentre.data.repository.PlayListRepository
import com.techdroidcentre.musicplayer.mapper.toPlaylist
import com.techdroidcentre.musicplayer.mapper.toPlaylistData
import com.techdroidcentre.musicplayer.model.PlayListData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListsViewModel @Inject constructor(
    private val playlistRepository: PlayListRepository
): ViewModel() {
    private val queryPlaylists = MutableLiveData(false)

    private val _playlists = Transformations.switchMap(queryPlaylists) { load ->
        if (load) {
            Transformations.map(playlistRepository.getPlayLists()) { playlists ->
                playlists.map { it.toPlaylistData() }
            }
        } else {
            MutableLiveData(emptyList())
        }
    }
    val playlists: LiveData<List<PlayListData>> = _playlists

    init {
        loadPlaylists(true)
    }

    private fun loadPlaylists(load: Boolean) {
        queryPlaylists.value = load
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(PlayListData(0L, name).toPlaylist())
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlayList(id)
        }
    }
}