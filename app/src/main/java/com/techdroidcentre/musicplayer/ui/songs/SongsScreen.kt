package com.techdroidcentre.musicplayer.ui.songs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.R
import com.techdroidcentre.musicplayer.model.SongData
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    viewModel.mediaId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        viewModel.subscribe(mediaId)
    }
    val songs by viewModel.songs.observeAsState()
    SongsCollection(
        songs = songs ?: mutableListOf(),
        playSong = viewModel::playSong,
        modifier = modifier
    )
}

@Composable
fun SongsCollection(
    songs: List<SongData>,
    playSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.padding(8.dp)
    ) {
        items(items = songs) { song ->
            SongItem(
                id = song.mediaId,
                title = song.title,
                artist = song.subtitle,
                album = song.description,
                coverArt = song.coverArt,
                playSong = playSong
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SongItem(
    id: String,
    title: String,
    artist: String,
    album: String,
    coverArt: Bitmap?,
    playSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        onClick = {
            playSong(id)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (coverArt != null) rememberAsyncImagePainter(model = coverArt) else painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(shape = MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$artist - $album",
                    style = MaterialTheme.typography.overline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun SongItemPreview() {
    MusicPlayerTheme {
        SongItem("id","Title", "Artist", "Album", null, {})
    }
}