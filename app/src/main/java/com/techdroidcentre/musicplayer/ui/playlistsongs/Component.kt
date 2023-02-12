package com.techdroidcentre.musicplayer.ui.playlistsongs

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.data.R
import com.techdroidcentre.musicplayer.model.SongData
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SongsListDialog(
    songs: List<SongData>,
    addToSelectedSongs: (SongData) -> Unit,
    removeFromSelectedSongs: (SongData) -> Unit,
    addSongs: () -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextButton(
                    onClick = dismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary)
                ) {
                    Text(text = "Cancel")
                }

                TextButton(
                    onClick = addSongs,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary)
                ) {
                    Text(text = "Done")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (songs.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier.fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(items = songs, key = { song -> song.mediaId }) { song ->
                        ToggleSongItem(
                            song = song,
                            addToSelectedSongs = addToSelectedSongs,
                            removeFromSelectedSongs = removeFromSelectedSongs
                        )
                    }
                }
            } else {
                EmptySongsList()
            }
        }
    }
}

@Composable
fun EmptySongsList(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_music_note_24),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
        )
        Text(
            text = "No music found.",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToggleSongItem(
    song: SongData,
    addToSelectedSongs: (SongData) -> Unit,
    removeFromSelectedSongs: (SongData) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(false) }
    ToggleSongItem(
        song = song,
        selected = selected,
        onSelectedChange = {
            selected = it
            if (it) addToSelectedSongs(song) else removeFromSelectedSongs(song)
        },
        modifier = modifier
    )
}

@Composable
fun ToggleSongItem(
    song: SongData,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (selected) Icons.Default.Check else Icons.Default.Add
    val colour = if (selected) MaterialTheme.colors.secondary else Color.LightGray

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (song.coverArt != null) rememberAsyncImagePainter(model = song.coverArt)
                else painterResource(id = R.drawable.ic_baseline_music_note_24),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(shape = MaterialTheme.shapes.small),
                // when coverArt is null default music-note drawable is shown. Apply a colorFilter...
                // ... to this drawable only
                colorFilter = if (song.coverArt == null) ColorFilter.tint(color = MaterialTheme.colors.onPrimary)
                else null
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
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .toggleable(
                        value = selected,
                        onValueChange = onSelectedChange,
                        role = Role.Button
                    )
                    .drawBehind {
                        drawCircle(color = colour)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Divider()
    }
}

@Preview("Songs list")
@Preview("Songs list (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SongsListDialogPreview() {
    MusicPlayerTheme {
        Surface{
            SongsListDialog(
                songs = List(15) {
                    SongData("$it", "content//:", "We Got Love", "Don Williams", "Country Time")
                },
                addToSelectedSongs = {},
                removeFromSelectedSongs = {},
                addSongs = {},
                dismiss = {},
            )
        }
    }
}