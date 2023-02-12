package com.techdroidcentre.musicplayer.ui.albums

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.model.MediaItemData

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
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Albums",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(items = albums) { album ->
                AlbumItem(
                    id = album.mediaId,
                    title = album.title,
                    subtitle = album.subtitle,
                    coverArt = album.coverArt,
                    navigateToSongs = navigateToSongs
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumItem(
    id: String,
    title: String,
    subtitle: String,
    coverArt: String,
    navigateToSongs: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbnail = getCoverArt(LocalContext.current, coverArt)

    Surface(
        modifier = modifier.width(150.dp),
        shape = MaterialTheme.shapes.small,
        onClick = {
            navigateToSongs(id)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = if (thumbnail == null) painterResource(id = com.techdroidcentre.data.R.drawable.ic_baseline_album_24)
                else rememberAsyncImagePainter(model = coverArt),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(shape = MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop,
                colorFilter = if (thumbnail == null) ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
                else null
            )
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getCoverArt(context: Context, coverArt: String): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.loadThumbnail(
                Uri.parse(coverArt),
                Size(300, 300),
                null
            )
        } else {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(coverArt))
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (ex: Exception) {
        null
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
