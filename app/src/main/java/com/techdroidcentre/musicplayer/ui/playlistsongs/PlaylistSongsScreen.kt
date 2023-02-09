package com.techdroidcentre.musicplayer.ui.playlistsongs

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.data.R
import com.techdroidcentre.musicplayer.model.SongData
import com.techdroidcentre.musicplayer.ui.songs.SongsViewModel
import com.techdroidcentre.musicplayer.util.getCoverArt
import com.techdroidcentre.musicplayer.util.getThumbnail

@Composable
fun PlaylistSongsScreen(
    viewModel: SongsViewModel,
    modifier: Modifier = Modifier
) {
    val playlistSongs by viewModel.playlistSongs.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        viewModel.mediaId.observe(LocalLifecycleOwner.current) {
            val mediaId = it ?: return@observe
            viewModel.subscribe(mediaId)
        }
        val songs by viewModel.songs.observeAsState()
        SongsListDialog(
            songs = songs ?: mutableListOf(),
            addToSelectedSongs = viewModel::addToSelectedSongs,
            removeFromSelectedSongs = viewModel::removeFromSelectedSongs,
            addSongs = {
                viewModel.addSongs()
                showDialog = !showDialog
            },
            dismiss = {
                showDialog = !showDialog
                viewModel.clearSelectedSongs()
            }
        )
    }

    PlaylistSongsScreen(
        songs = playlistSongs ?: mutableListOf(),
        deleteSong = viewModel::deletePlaylistSong,
        showDialog = { showDialog = !showDialog },
        modifier = modifier
    )
}

@Composable
fun PlaylistSongsScreen(
    songs: List<SongData>,
    deleteSong: (String) -> Unit,
    showDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Songs",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.weight(1F)
            )
            IconButton(
                onClick = showDialog,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New playlist",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (songs.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                items(items = songs, key = { song -> song.mediaId }) { song ->
                    PlaylistSongItem(
                        song = song,
                        deleteSong = deleteSong
                    )
                }
            }
        } else {
            EmptyPlaylist()
        }
    }
}

@Composable
fun EmptyPlaylist(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = com.techdroidcentre.musicplayer.R.drawable.baseline_queue_music_24),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
        )
        Text(
            text = "There is no music in this playlist. Click + to add music.",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PlaylistSongItem(
    song: SongData,
    deleteSong: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val coverArt = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getThumbnail(context, song.coverArt)
        } else {
            getCoverArt(song.coverArt)
        }
    } catch (ex: Exception) {
        null
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (coverArt != null) rememberAsyncImagePainter(model = coverArt) else painterResource(id = R.drawable.ic_baseline_music_note_24),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(shape = MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${song.subtitle} - ${song.description}",
                    style = MaterialTheme.typography.overline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(contentAlignment = Alignment.Center) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                }
                PlaylistSongDropdownMenu(
                    expanded = expanded,
                    dismiss = { expanded = !expanded },
                    deleteSong = {
                        deleteSong(song.mediaId)
                        expanded = !expanded
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Divider()
    }
}

@Composable
fun PlaylistSongDropdownMenu(
    expanded: Boolean,
    dismiss: () -> Unit,
    deleteSong: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = dismiss,
        modifier = modifier
    ) {
        DropdownMenuItem(onClick = deleteSong) {
            Text(text = "Remove from playlist")
        }
    }
}