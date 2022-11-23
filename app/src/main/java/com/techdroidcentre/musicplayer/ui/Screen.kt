package com.techdroidcentre.musicplayer.ui

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
}