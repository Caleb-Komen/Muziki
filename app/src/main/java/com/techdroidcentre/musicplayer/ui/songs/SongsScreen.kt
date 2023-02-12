package com.techdroidcentre.musicplayer.ui.songs

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.model.PlayListViewState
import com.techdroidcentre.musicplayer.model.SongData
import com.techdroidcentre.musicplayer.ui.playlists.CreatePlaylistDialog
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun SongsScreen(
    viewModel: SongsViewModel,
    modifier: Modifier = Modifier
) {
    viewModel.mediaId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        viewModel.subscribe(mediaId)
    }

    var mediaUri = ""
    val contentResolver = LocalContext.current.contentResolver
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == RESULT_OK) {
            viewModel.deleteSong(mediaUri)
            viewModel.deletePlaylistSong(mediaUri)
        }
    }
    val songs by viewModel.songs.observeAsState()
    val playlists by viewModel.playlists.observeAsState()

    SongsCollection(
        songs = songs ?: mutableListOf(),
        playlists = playlists ?: mutableListOf(),
        playSong = {
            viewModel.playSong(it)
        },
        deleteSong = {
            mediaUri = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                deleteSong(contentResolver, launcher, it)
            } else {
                val result = deleteSong(contentResolver, it)
                if (result) {
                    viewModel.deleteSong(it)
                    viewModel.deletePlaylistSong(it)
                }
            }
        },
        addToPlaylist = viewModel::addSongToPlaylist,
        createPlaylist = viewModel::createPlaylist,
        modifier = modifier
    )
}

@Composable
fun SongsCollection(
    songs: List<SongData>,
    playlists: List<PlayListViewState>,
    playSong: (String) -> Unit,
    deleteSong: (String) -> Unit,
    addToPlaylist: (Long, SongData) -> Unit,
    createPlaylist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "Songs",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(items = songs, key = { song -> song.mediaId }) { song ->
            SongItem(
                id = song.mediaId,
                title = song.title,
                artist = song.subtitle,
                album = song.description,
                coverArt = song.coverArt,
                playSong = playSong,
                playlists = playlists,
                deleteSong = {
                    deleteSong(song.uri)
                },
                addToPlaylist = {
                    addToPlaylist(it, song)
                },
                createPlaylist = createPlaylist
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
    playlists: List<PlayListViewState>,
    playSong: (String) -> Unit,
    deleteSong: () -> Unit,
    addToPlaylist: (Long) -> Unit,
    createPlaylist: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlaylistsDialog by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteSongConfirmationDialog(dismiss = { showDeleteDialog = !showDeleteDialog }, deleteSong = {
            deleteSong()
            showDeleteDialog = !showDeleteDialog
        })
    }
    if (showPlaylistsDialog) {
        PlaylistsListDialog(
            playlists = playlists,
            onPlaylistClick = {
                addToPlaylist(it)
                showPlaylistsDialog = !showPlaylistsDialog
            },
            createPlaylist = { showCreatePlaylistDialog = !showCreatePlaylistDialog },
            dismiss = { showPlaylistsDialog = !showPlaylistsDialog }
        )
    }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            name = playlistName,
            onNameChange = { playlistName = it },
            createPlaylist = {
                createPlaylist(it)
                showCreatePlaylistDialog = !showCreatePlaylistDialog
            },
            dismiss = {
                showCreatePlaylistDialog = !showCreatePlaylistDialog
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        onClick = {
            playSong(id)
        },
        color = Color.Transparent
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = if (coverArt != null) rememberAsyncImagePainter(model = coverArt)
                    else painterResource(id = com.techdroidcentre.data.R.drawable.ic_baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(shape = MaterialTheme.shapes.small),
                    colorFilter = if (coverArt == null) ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
                    else null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
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
                Spacer(modifier = Modifier.width(8.dp))
                Box(contentAlignment = Alignment.Center) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }
                    SongDropdownMenu(
                        expanded = expanded,
                        dismiss = { expanded = !expanded },
                        deleteSong = {
                            showDeleteDialog = !showDeleteDialog
                            expanded = !expanded
                        },
                        addToPlaylist = {
                            showPlaylistsDialog = !showPlaylistsDialog
                            expanded = !expanded
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Divider()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun deleteSong(
    contentResolver: ContentResolver,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    uri: String
) {
    val uris = mutableListOf(Uri.parse(uri))
    val pendingIntent = MediaStore.createDeleteRequest(contentResolver, uris)
    val senderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
    launcher.launch(senderRequest)
}

fun deleteSong(
    contentResolver: ContentResolver,
    uri: String
): Boolean {
    val result = contentResolver.delete(Uri.parse(uri), null, null)
    return result > 0
}

@Preview
@Composable
fun SongItemPreview() {
    MusicPlayerTheme {
        SongItem(
            "id","Title", "Artist", "Album", null, emptyList(), {}, {}, {}, {}
        )
    }
}