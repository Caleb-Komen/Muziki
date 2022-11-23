package com.techdroidcentre.musicplayer.ui.home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.techdroidcentre.musicplayer.model.MediaItemData

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    homeViewModel.rootId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        homeViewModel.subscribe(mediaId)
    }
    val mediaItems by homeViewModel.mediaItems.observeAsState()

    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        BrowsableItems(
            listItems = mediaItems ?: mutableListOf()
        )
    }
}

@Composable
fun BrowsableItems(
    listItems: List<MediaItemData>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(items = listItems) { item ->
            HomeScreenItem(
                coverArt = item.coverArt, 
                title = item.title
            )
        }
    }
}

@Composable
fun HomeScreenItem(
    coverArt: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(Uri.parse(coverArt)),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1F)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenItemPreview() {
    HomeScreenItem(
        coverArt = "android.resource://com.techdroidcentre.musicplayer/drawable/ic_launcher_background",
        title = "Albums"
    )
}

@Preview(showBackground = true)
@Composable
fun BrowsableItemsPreview() {
    BrowsableItems(listItems)
}

val listItems = listOf(
    MediaItemData("1", "Songs", "", "", true, ""),
    MediaItemData("2", "Albums", "", "", true, ""),
    MediaItemData("3", "Artists", "", "", true, ""),
)