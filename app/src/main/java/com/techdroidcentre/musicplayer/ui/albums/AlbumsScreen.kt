package com.techdroidcentre.musicplayer.ui.albums

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.musicplayer.R
import com.techdroidcentre.musicplayer.model.MediaItemData
import com.techdroidcentre.musicplayer.ui.components.AlbumArtistItem

@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    viewModel.mediaId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        viewModel.subscribe(mediaId)
    }
    val albums by viewModel.albums.observeAsState()
    AlbumsCollection(
        albums = albums ?: mutableListOf(),
        modifier = modifier
    )
}

@Composable
fun AlbumsCollection(
    albums: List<MediaItemData>,
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
                subtitle = album.subtitle
            )
        }
    }
}

@Composable
fun AlbumItem(
    title: String,
    artist: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.width(150.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.musica),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(shape = MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
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
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AlbumItemPreview() {
    AlbumItem("Album", "Artist")
}