package com.techdroidcentre.musicplayer.mapper

import com.techdroidcentre.data.model.PlayList
import com.techdroidcentre.musicplayer.model.PlayListViewState

fun PlayListViewState.toModel(): PlayList {
    return PlayList(id = id, name = name)
}

fun PlayList.toViewState(): PlayListViewState {
    return PlayListViewState(id = id, name = name)
}