package com.techdroidcentre.musicplayer.ui.songs

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.model.SongData
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme
import com.techdroidcentre.musicplayer.util.getCoverArt
import com.techdroidcentre.musicplayer.util.getThumbnail

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = hiltViewModel()
) {
    viewModel.mediaId.observe(LocalLifecycleOwner.current) {
        val mediaId = it ?: return@observe
        viewModel.subscribe(mediaId)
    }

    var mediaUri = ""
    val contentResolver = LocalContext.current.contentResolver
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
        if (it.resultCode == RESULT_OK) viewModel.deleteSong(mediaUri)
    }
    val songs by viewModel.songs.observeAsState()
    SongsCollection(
        songs = songs ?: mutableListOf(),
        playSong = viewModel::playSong,
        deleteSong = {
            mediaUri = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                deleteSong(contentResolver, launcher, it)
            } else {
                val result = deleteSong(contentResolver, it)
                if (result) viewModel.deleteSong(it)
            }
        },
        modifier = modifier
    )
}

@Composable
fun SongsCollection(
    songs: List<SongData>,
    playSong: (String) -> Unit,
    deleteSong: (String) -> Unit,
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
                artUri = song.coverArt,
                playSong = playSong,
                deleteSong = {
                    deleteSong(song.uri)
                }
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
    artUri: String,
    playSong: (String) -> Unit,
    deleteSong: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val coverArt = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getThumbnail(context, artUri)
        } else {
            getCoverArt(artUri)
        }
    } catch (ex: Exception) {
        null
    }
    if (showDialog) {
        DeleteSongConfirmationDialog(dismiss = { showDialog = !showDialog }, deleteSong = {
            deleteSong()
            showDialog = !showDialog
        })
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
                    painter = if (coverArt != null) rememberAsyncImagePainter(model = coverArt) else painterResource(id = com.techdroidcentre.data.R.drawable.ic_baseline_music_note_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(shape = MaterialTheme.shapes.small)
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
                            showDialog = !showDialog
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
        SongItem("id","Title", "Artist", "Album", "", {}, {})
    }
}