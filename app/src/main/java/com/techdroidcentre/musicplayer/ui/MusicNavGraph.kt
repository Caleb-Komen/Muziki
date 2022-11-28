package com.techdroidcentre.musicplayer.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techdroidcentre.musicplayer.ui.albums.AlbumsScreen
import com.techdroidcentre.musicplayer.ui.artists.ArtistsScreen
import com.techdroidcentre.musicplayer.ui.home.HomeScreen
import com.techdroidcentre.musicplayer.ui.songs.SongsScreen

@Composable
fun MusicNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
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
            AlbumsScreen()
        }

        composable(
            route = Screen.ArtistsScreen.route,
            arguments = listOf(
                navArgument(MEDIA_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            ArtistsScreen()
        }

        composable(
            route = Screen.SongsScreen.route,
            arguments = listOf(
                navArgument(MEDIA_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            SongsScreen()
        }
    }
}