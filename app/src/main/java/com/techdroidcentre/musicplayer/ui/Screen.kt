package com.techdroidcentre.musicplayer.ui

const val ALBUMS_SCREEN_ROUTE = "albums"
const val ARTISTS_SCREEN_ROUTE = "artists"
const val SONGS_SCREEN_ROUTE = "songs"
const val PLAYLIST_SONGS_SCREEN_ROUTE = "playlist_songs"
const val MEDIA_ID_KEY = "id"
const val PLAYLIST_ID_KEY = "playlist_id"

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

    object PlaylistsScreen: Screen("playlists")

    object PlaylistSongsScreen: Screen("$PLAYLIST_SONGS_SCREEN_ROUTE/{$PLAYLIST_ID_KEY}&{$MEDIA_ID_KEY}") {
        fun passId(playlistId: Long, mediaId: String): String {
            return "$PLAYLIST_SONGS_SCREEN_ROUTE/$playlistId&$mediaId"
        }
    }
}