package com.techdroidcentre.musicplayer.ui.artists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.model.ArtistData
import com.techdroidcentre.musicplayer.util.getCoverArt
import com.techdroidcentre.musicplayer.util.getThumbnail

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
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Artists",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = artists) { artist ->
                ArtistItem(
                    id = artist.mediaId,
                    title = artist.title,
                    subtitle = artist.subtitle,
                    artUri = artist.coverArt,
                    navigateToSongs = navigateToSongs
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ArtistItem(
    id: String,
    title: String,
    subtitle: String,
    artUri: String,
    navigateToSongs: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coverArt = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getThumbnail(context, artUri)
        } else {
            getCoverArt(artUri)
        }
    } catch (ex: Exception) {
        null
    }

    Surface(
        modifier = modifier.width(150.dp),
        shape = MaterialTheme.shapes.small,
        onClick = {
            navigateToSongs(id)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = if (coverArt == null) painterResource(id = com.techdroidcentre.data.R.drawable.ic_account_music) else rememberAsyncImagePainter(model = coverArt),
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
                text = subtitle,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}