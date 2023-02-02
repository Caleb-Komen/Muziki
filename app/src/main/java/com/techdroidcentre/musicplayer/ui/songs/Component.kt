package com.techdroidcentre.musicplayer.ui.songs

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SongDropdownMenu(
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
            Text(text = "Delete")
        }
        DropdownMenuItem(onClick = { /*TODO*/ }) {
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