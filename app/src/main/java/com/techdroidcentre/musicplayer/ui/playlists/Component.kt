package com.techdroidcentre.musicplayer.ui.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun CreatePlaylistDialog(
    name: String,
    onNameChange: (String) -> Unit,
    createPlaylist: (String) -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = dismiss) {
        Box(modifier = modifier.background(MaterialTheme.colors.surface)) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "New Playlist",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = {
                        Text(text = "Playlist Name")
                    },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = dismiss
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = { createPlaylist(name) },
                        enabled = name.isNotEmpty()
                    ) {
                        Text(text = "Create")
                    }
                }
            }
        }
    }
}
