package com.techdroidcentre.musicplayer.ui

const val ALBUMS_SCREEN_ROUTE = "albums"
const val ARTISTS_SCREEN_ROUTE = "artists"
const val SONGS_SCREEN_ROUTE = "songs"
const val MEDIA_ID_KEY = "id"

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home")

    object AlbumsScreen: Screen("$ALBUMS_SCREEN_ROUTE/{$MEDIA_ID_KEY}") {
        fun passId(mediaId: String): String {
            return "$ALBUMS_SCREEN_ROUTE/$mediaId"
        }
    }

    object ArtistsScreen: Screen("$ARTISTS_SCREEN_ROUTE/{$MEDIA_ID_KEY}") {
        fun passId(mediaId: String): String {
            return "$ARTISTS_SCREEN_ROUTE/$mediaId"
        }
    }

    object SongsScreen: Screen("$SONGS_SCREEN_ROUTE/{$MEDIA_ID_KEY}") {
        fun passId(mediaId: String): String {
            return "$SONGS_SCREEN_ROUTE/$mediaId"
        }
    }
}