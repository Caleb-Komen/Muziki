package com.techdroidcentre.musicplayer.ui.songs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.techdroidcentre.musicplayer.model.PlayListData

@Composable
fun SongDropdownMenu(
    expanded: Boolean,
    dismiss: () -> Unit,
    deleteSong: () -> Unit,
    addToPlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = dismiss,
        modifier = modifier
    ) {
        DropdownMenuItem(onClick = deleteSong) {
            Text(text = "Delete")
        }
        DropdownMenuItem(onClick = addToPlaylist) {
            Text(text = "Add to a playlist")
        }
    }

}

@Composable
fun DeleteSongConfirmationDialog(
    dismiss: () -> Unit,
    deleteSong: () -> Unit
) {
    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            Button(onClick = deleteSong) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = dismiss) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Delete Song")
        },
        text = {
            Text(text = "Continue?")
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlaylistsListDialog(
    playlists: List<PlayListData>,
    onPlaylistClick: (Long) -> Unit,
    createPlaylist: () -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextButton(
                    onClick = dismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary)
                ) {
                    Text(text = "Cancel")
                }

                TextButton(
                    onClick = createPlaylist,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary)
                ) {
                    Text(text = "Create")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (playlists.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(items = playlists, key = { playlist -> playlist.id }) { playlist ->
                        PlaylistItem(playlist = playlist, onPlaylistClick = onPlaylistClick)
                    }
                }
            } else {
                EmptyPlaylistsList()
            }
        }
    }
}

@Composable
fun EmptyPlaylistsList(modifier: Modifier = Modifier) {
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
            text = "No playlist found.",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PlaylistItem(
    playlist: PlayListData,
    onPlaylistClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onPlaylistClick(playlist.id) }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.height(56.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Divider()
    }
}

@Preview
@Composable
fun PlaylistItemPreview() {
    Surface {
        PlaylistItem(
            PlayListData(1L, "Country"), {}
        )
    }
}
