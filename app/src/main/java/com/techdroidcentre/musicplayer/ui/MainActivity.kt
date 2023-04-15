package com.techdroidcentre.musicplayer.ui

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                var isPermissionGranted by rememberSaveable { mutableStateOf(false) }

                MusicPlayerTheme {
                    Surface {
                        PermissionDialog(context = this) { permissionAction ->
                            isPermissionGranted = when (permissionAction) {
                                PermissionAction.PermissionGranted -> {
                                    true
                                }
                                PermissionAction.PermissionDenied -> {
                                    false
                                }
                            }
                        }
                        if (isPermissionGranted) {
                            MainApp()
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "This app requires access to your media files in order to browse and play songs",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        override fun onResume() {
            super.onResume()
            volumeControlStream = AudioManager.STREAM_MUSIC
        }
}