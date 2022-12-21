package com.techdroidcentre.musicplayer.ui.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.musicplayer.model.ArtistData
import com.techdroidcentre.musicplayer.model.MediaItemData
import com.techdroidcentre.musicplayer.ui.components.AlbumArtistItem
import com.techdroidcentre.musicplayer.ui.components.ArtistItem

@Composable
fun ArtistsScreen(
    modifier: Modifier = Modifier,
    navigateToSongs: (String) -> Unit,
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    viewModel.mediaId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        viewModel.subscribe(mediaId)
    }
    val artists by viewModel.artists.observeAsState()
    ArtistsCollection(
        artists = artists ?: mutableListOf(),
        navigateToSongs = navigateToSongs,
        modifier = modifier
    )
}

@Composable
fun ArtistsCollection(
    artists: List<ArtistData>,
    navigateToSongs: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(8.dp)
    ) {
        items(items = artists) { artist ->
            ArtistItem(
                id = artist.mediaId,
                title = artist.title,
                subtitle = artist.subtitle,
                coverArt = artist.coverArt,
                navigateToSongs = navigateToSongs
            )
        }
    }
}