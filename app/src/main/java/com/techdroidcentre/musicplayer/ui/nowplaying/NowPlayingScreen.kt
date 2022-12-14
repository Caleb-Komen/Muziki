package com.techdroidcentre.musicplayer.ui.nowplaying

import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techdroidcentre.musicplayer.R
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme
import com.techdroidcentre.musicplayer.util.lerp

val closedSheetHeight = 56.dp

@Composable
fun NowPlayingSheet(
    openFraction: Float,
    height: Float
) {
    val collapsedSheetHeight = with(LocalDensity.current) { closedSheetHeight.toPx() }
    val sheetHeight = collapsedSheetHeight + WindowInsets.systemBars.getBottom(LocalDensity.current)
    val offsetX = lerp(0f, 0f, 0f, 0.15f, openFraction)
    val offsetY = lerp(height - sheetHeight, 0f, openFraction)

    Surface(
        modifier = Modifier.graphicsLayer {
            translationX = offsetX
            translationY = offsetY
        }
    ) {
        NowPlayingScreen(openFraction = openFraction)
    }
}

@Composable
fun NowPlayingScreen(
    openFraction: Float,
    modifier: Modifier = Modifier,
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val playbackState by viewModel.playbackState.observeAsState()
    val metadata by viewModel.metadata.observeAsState()

    val isPlaying = playbackState?.state == PlaybackStateCompat.STATE_PLAYING
    val title: String
    val subtitle: String

    if (metadata == null) {
        title = "Not Playing"
        subtitle = ""
    } else {
        title = metadata?.getString(METADATA_KEY_TITLE) ?: "Unknown"
        subtitle = metadata?.getString(METADATA_KEY_ARTIST) ?: "Unknown"
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        val lessonsAlpha = lerp(0f, 1f, 0.2f, 0.8f, openFraction)
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .graphicsLayer { alpha = lessonsAlpha }
        ) {
            PlaybackMetaData(
                title = title,
                subtitle = subtitle,
                modifier = Modifier.weight(7f)
            )
            PlaybackControls(
                isPlaying = isPlaying,
                playOrPause = viewModel::playOrPause,
                skipToNext = viewModel::skipToNext,
                skipToPrevious = viewModel::skipToPrevious,
                modifier = Modifier.weight(3f)
            )
        }

        val fabAlpha = lerp(1f, 0f, 0f, 0.15f, openFraction)
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(closedSheetHeight)
                .graphicsLayer { alpha = fabAlpha }
        ) {
            NowPlayingClosedSheet(
                title = title,
                isPlaying = isPlaying,
                playOrPause = viewModel::playOrPause
            )
        }
    }
}

@Composable
fun PlaybackMetaData(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.musica),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h5
        )
        Text(
            text = subtitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body1
        )
        PlaybackPositionIndicator("0:00", "3:46")
    }
}

@Composable
fun PlaybackPositionIndicator(
    position: String,
    duration: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = 0.5f,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box( modifier = Modifier.fillMaxWidth()) {
            Text(
                text = position,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            Text(
                text = duration,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    playOrPause: () -> Unit,
    skipToNext: () -> Unit,
    skipToPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = skipToPrevious,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_skip_previous_24),
                contentDescription = "skip previous",
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = playOrPause,
            modifier = Modifier.size(48.dp)
        ) {
            if (isPlaying) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_pause_24),
                    contentDescription = "play",
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24),
                    contentDescription = "play",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = skipToNext
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_skip_next_24),
                contentDescription = "skip next",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun NowPlayingClosedSheet(
    title: String,
    isPlaying: Boolean,
    playOrPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(56.dp)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = playOrPause,
                modifier = Modifier.size(48.dp)
                    .padding(horizontal = 8.dp)
            ) {
                if (isPlaying) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_pause_24),
                        contentDescription = "play",
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24),
                        contentDescription = "play",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NowPlayingSheetClosedPreview() {
    MusicPlayerTheme {
        NowPlayingScreen(1f)
    }
}

@Preview(showBackground = true)
@Composable
fun NowPlayingSheetOpenPreview() {
    MusicPlayerTheme {
        NowPlayingScreen(1f)
    }
}
