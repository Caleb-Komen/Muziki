package com.techdroidcentre.musicplayer.ui.nowplaying

import android.os.Build
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.R
import com.techdroidcentre.musicplayer.model.NowPlayingMetadata
import com.techdroidcentre.musicplayer.ui.theme.MusicPlayerTheme
import com.techdroidcentre.musicplayer.util.getCoverArt
import com.techdroidcentre.musicplayer.util.getThumbnail
import com.techdroidcentre.musicplayer.util.lerp
import kotlin.math.floor

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
    val isPlaying by viewModel.isPlaying.observeAsState()
    val nowPlayingMetadata by viewModel.mediaMetadata.observeAsState()
    val mediaPosition by viewModel.mediaPosition.observeAsState()

    Box(modifier = Modifier.fillMaxWidth()) {
        val openAlpha = lerp(0f, 1f, 0.2f, 0.8f, openFraction)
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .graphicsLayer { alpha = openAlpha }
        ) {
            PlaybackMetaData(
                nowPlayingMetadata = nowPlayingMetadata!!,
                mediaPosition = mediaPosition!!,
                onValueChange = viewModel::seekTo,
                modifier = Modifier.weight(7f)
            )
            PlaybackControls(
                isPlaying = isPlaying!!,
                playOrPause = viewModel::playOrPause,
                skipToNext = viewModel::skipToNext,
                skipToPrevious = viewModel::skipToPrevious,
                modifier = Modifier.weight(3f)
            )
        }

        val collapsedAlpha = lerp(1f, 0f, 0f, 0.15f, openFraction)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(closedSheetHeight)
                .graphicsLayer { alpha = collapsedAlpha }
        ) {
            NowPlayingClosedSheet(
                title = nowPlayingMetadata!!.title,
                isPlaying = isPlaying!!,
                playOrPause = viewModel::playOrPause
            )
        }
    }
}

@Composable
fun PlaybackMetaData(
    nowPlayingMetadata: NowPlayingMetadata,
    mediaPosition: Long,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        val context = LocalContext.current
        val coverArt = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getThumbnail(context, nowPlayingMetadata.albumArt)
            } else {
                getCoverArt(nowPlayingMetadata.albumArt)
            }
        } catch (ex: Exception) {
            null
        }
        Image(
            painter = if (coverArt != null) rememberAsyncImagePainter(coverArt)
            else painterResource(id = com.techdroidcentre.data.R.drawable.ic_baseline_music_note_24),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
                .clip(shape = RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = nowPlayingMetadata.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.h5
        )
        Text(
            text = nowPlayingMetadata.subtitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body1
        )
        PlaybackPositionIndicator(mediaPosition, nowPlayingMetadata.duration, onValueChange = onValueChange)
    }
}

@Composable
fun PlaybackPositionIndicator(
    position: Long,
    duration: Long,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = position.toFloat(),
            onValueChange = onValueChange,
            valueRange = 0f..duration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box( modifier = Modifier.fillMaxWidth()) {
            Text(
                text = toDurationString(position),
                modifier = Modifier.align(Alignment.BottomStart)
            )
            Text(
                text = toDurationString(duration),
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
    Box(modifier = modifier.shadow(elevation = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(closedSheetHeight)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = playOrPause,
                modifier = Modifier
                    .size(48.dp)
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
// format duration to minutes and seconds
@Composable
private fun toDurationString(duration: Long): String {
    val totalSeconds = floor(duration / 1E3).toInt()
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds - (minutes * 60)
    return stringResource(R.string.duration_format, minutes, remainingSeconds)
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
