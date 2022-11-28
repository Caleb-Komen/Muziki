package com.techdroidcentre.musicplayer.ui.albums

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.musicplayer.model.MediaItemData
import com.techdroidcentre.musicplayer.ui.components.AlbumArtistItem

@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    navigateToSongs: (String) -> Unit,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    viewModel.mediaId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        viewModel.subscribe(mediaId)
    }
    val albums by viewModel.albums.observeAsState()
    AlbumsCollection(
        albums = albums ?: mutableListOf(),
        navigateToSongs = navigateToSongs,
        modifier = modifier
    )
}

@Composable
fun AlbumsCollection(
    albums: List<MediaItemData>,
    navigateToSongs: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(8.dp)
    ) {
        items(items = albums) { album ->
            AlbumArtistItem(
                id = album.mediaId,
                title = album.title,
                subtitle = album.subtitle,
                navigateToSongs = navigateToSongs
            )
        }
    }
}

@Preview
@Composable
fun AlbumsCollectionPreview() {
    AlbumsCollection(
        albums = listOf(
            MediaItemData("1", "Album 1", "Artist 1", "", true, ""),
            MediaItemData("2", "Album 2", "Artist 2", "", true, ""),
            MediaItemData("3", "Album 3", "Artist 3", "", true, "")
        ),
        navigateToSongs = {}
    )
}
