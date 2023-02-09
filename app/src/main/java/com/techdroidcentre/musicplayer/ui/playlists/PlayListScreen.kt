@file:OptIn(ExperimentalMaterialApi::class)

package com.techdroidcentre.musicplayer.ui.playlists

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.musicplayer.R
import com.techdroidcentre.musicplayer.model.PlayListViewState
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme
import kotlin.math.roundToInt

@Composable
fun PlaylistScreen(
    navigateToSongs: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayListsViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.observeAsState()
    var playlistName by remember { mutableStateOf("")}
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        CreatePlaylistDialog(
            name = playlistName,
            onNameChange = { playlistName = it },
            createPlaylist = {
                viewModel.createPlaylist(it)
                showDialog = !showDialog
            },
            dismiss = { showDialog = !showDialog }
        )
    }

    PlaylistScreen(
        playlists = playlists ?: mutableListOf(),
        showDialog = { showDialog = !showDialog},
        navigateToSongs = navigateToSongs,
        deletePlaylist = viewModel::deletePlaylist,
        modifier = modifier
    )
}

@Composable
fun PlaylistScreen(
    playlists: List<PlayListViewState>,
    showDialog: () -> Unit,
    navigateToSongs: (Long) -> Unit,
    deletePlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlists",
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
        if (playlists.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.fillMaxSize()
            ) {
                items(items = playlists, key = { playlist -> playlist.id }) {playlist ->
                    PlaylistItem(
                        id = playlist.id,
                        name = playlist.name,
                        navigateToSongs = navigateToSongs,
                        deletePlaylist = deletePlaylist
                    )
                }
            }
        } else {
            EmptyPlaylists()
        }
    }
}

@Composable
fun EmptyPlaylists(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_queue_music_24),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
        )
        Text(
            text = "No playlists. Click + to create playlist.",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun PlaylistItem(
    id: Long,
    name: String,
    navigateToSongs: (Long) -> Unit,
    deletePlaylist: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 64.dp
    val widthPx = with(LocalDensity.current) {
        64.dp.toPx()
    }
    val swipeableState = rememberSwipeableState(initialValue = "hide")
    val anchors = mapOf(0f to "hide", -widthPx to "show")

    Column(
        modifier = modifier.swipeable(
            state = swipeableState,
            anchors = anchors,
            orientation = Orientation.Horizontal,
            thresholds = { _, _ ->
                FractionalThreshold(0.5f)
            }
        )
            .clickable {
                navigateToSongs(id)
            },
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.height(itemHeight),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .width(itemHeight)
                    .fillMaxHeight()
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { deletePlaylist(id)}) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Playlist"
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .offset {
                        IntOffset(swipeableState.offset.value.roundToInt(), 0)
                    }
                    .background(MaterialTheme.colors.surface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_queue_music_24),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Divider()
    }
}

@Preview("Playlist screen", device = Devices.PIXEL_4)
@Preview("Playlist screen (dark)", uiMode = UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun PlaylistScreenPreview() {
    MusicPlayerTheme {
        Surface {
            PlaylistScreen(
                playlists = List(15) { PlayListViewState(it.toLong(), "Playlist $it") },
                {}, {}, {}
            )

        }
    }
}

@Preview("Empty playlist screen", device = Devices.PIXEL_4)
@Preview("Empty playlist screen (dark)", uiMode = UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun EmptyPlaylistScreenPreview() {
    MusicPlayerTheme {
        Surface {
            PlaylistScreen(
                playlists = emptyList(), {}, {}, {}
            )

        }
    }
}