package com.techdroidcentre.musicplayer.ui.playlists

import androidx.lifecycle.*
import com.techdroidcentre.data.repository.PlayListRepository
import com.techdroidcentre.musicplayer.mapper.toModel
import com.techdroidcentre.musicplayer.mapper.toViewState
import com.techdroidcentre.musicplayer.model.PlayListViewState
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
                playlists.map { it.toViewState() }
            }
        } else {
            MutableLiveData(emptyList())
        }
    }
    val playlists: LiveData<List<PlayListViewState>> = _playlists

    init {
        loadPlaylists(true)
    }

    private fun loadPlaylists(load: Boolean) {
        queryPlaylists.value = load
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(PlayListViewState(0L, name).toModel())
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlayList(id)
        }
    }
}