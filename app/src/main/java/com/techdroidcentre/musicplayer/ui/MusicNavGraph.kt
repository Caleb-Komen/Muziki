package com.techdroidcentre.musicplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techdroidcentre.data.SONGS_ROOT
import com.techdroidcentre.musicplayer.ui.albums.AlbumsScreen
import com.techdroidcentre.musicplayer.ui.artists.ArtistsScreen
import com.techdroidcentre.musicplayer.ui.home.HomeScreen
import com.techdroidcentre.musicplayer.ui.playlists.PlaylistScreen
import com.techdroidcentre.musicplayer.ui.playlistsongs.PlaylistSongsScreen
import com.techdroidcentre.musicplayer.ui.songs.SongsScreen
import com.techdroidcentre.musicplayer.ui.songs.SongsViewModel

@Composable
fun MusicNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(
                navigateToAlbums = {
                    navController.navigate(Screen.AlbumsScreen.passId(it))
                },
                navigateToArtists = {
                    navController.navigate(Screen.ArtistsScreen.passId(it))
                },
                navigateToSongs = {
                    navController.navigate(Screen.SongsScreen.passId(it))
                },
                navigateToPlaylists = {
                    navController.navigate(Screen.PlaylistsScreen.route)
                }
            )
        }

        composable(
            route = Screen.AlbumsScreen.route,
            arguments = listOf(
                navArgument(MEDIA_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            AlbumsScreen(
                navigateToSongs = {
                    navController.navigate(Screen.SongsScreen.passId(it))
                }
            )
        }

        composable(
            route = Screen.ArtistsScreen.route,
            arguments = listOf(
                navArgument(MEDIA_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            ArtistsScreen(
                navigateToSongs = {
                    navController.navigate(Screen.SongsScreen.passId(it))
                }
            )
        }

        composable(
            route = Screen.SongsScreen.route,
            arguments = listOf(
                navArgument(MEDIA_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            val songsViewModel: SongsViewModel = hiltViewModel()
            SongsScreen(viewModel = songsViewModel)
        }
        
        composable(
            route = Screen.PlaylistsScreen.route
        ) {
            PlaylistScreen(
                navigateToSongs = {
                    navController.navigate(Screen.PlaylistSongsScreen.passId(it, SONGS_ROOT))
                }
            )
        }

        composable(
            route = Screen.PlaylistSongsScreen.route,
            arguments = listOf(
                navArgument(PLAYLIST_ID_KEY) {
                    type = NavType.LongType
                },
                navArgument(MEDIA_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            val songsViewModel: SongsViewModel = hiltViewModel()
            PlaylistSongsScreen(viewModel = songsViewModel)
        }
    }
}