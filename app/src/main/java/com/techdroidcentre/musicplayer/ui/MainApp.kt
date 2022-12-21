package com.techdroidcentre.musicplayer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.rememberNavController
import com.techdroidcentre.musicplayer.ui.nowplaying.NowPlayingSheet
import com.techdroidcentre.musicplayer.ui.nowplaying.closedSheetHeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainApp() {
    BoxWithConstraints {
        val navController = rememberNavController()
        val sheetState = rememberSwipeableState(SheetState.CLOSED)
        val collapsedSheetHeight = with(LocalDensity.current) { closedSheetHeight.toPx() }
        val dragRange = constraints.maxHeight - collapsedSheetHeight
        val scope = rememberCoroutineScope()

        BackHandler(enabled = sheetState.currentValue == SheetState.OPEN) {
            scope.launch {
                sheetState.animateTo(SheetState.CLOSED)
            }
        }

        Box(
            Modifier.swipeable(
                state = sheetState,
                anchors = mapOf(
                    0f to SheetState.CLOSED,
                    -dragRange to SheetState.OPEN
                ),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Vertical
            )
        ) {
            val openFraction = if (sheetState.offset.value.isNaN()) {
                0f
            } else {
                -sheetState.offset.value / dragRange
            }.coerceIn(0f, 1f)
            MusicNavGraph(navController = navController, modifier = Modifier.padding(bottom = closedSheetHeight))
            NowPlayingSheet(
                openFraction = openFraction,
                height = this@BoxWithConstraints.constraints.maxHeight.toFloat()
            )
        }
    }
}

private enum class SheetState {
    OPEN,
    CLOSED
}