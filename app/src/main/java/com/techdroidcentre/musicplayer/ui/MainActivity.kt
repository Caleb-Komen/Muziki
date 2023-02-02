package com.techdroidcentre.musicplayer.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Surface
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ isGranted ->
                if (isGranted.containsValue(false)) {
                    Log.d("MainActivity", "onCreate: Permission denied")
                } else {
                    setContent {
                        MusicPlayerTheme {
                            Surface {
                                MainApp()
                            }
                        }
                    }
                }
            }.launch(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE))

        }

        override fun onResume() {
            super.onResume()
            volumeControlStream = AudioManager.STREAM_MUSIC
        }
}