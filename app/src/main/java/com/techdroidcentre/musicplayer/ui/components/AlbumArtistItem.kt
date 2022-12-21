package com.techdroidcentre.musicplayer.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.techdroidcentre.musicplayer.R
import java.io.InputStream

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlbumArtistItem(
    id: String,
    title: String,
    subtitle: String,
    coverArt: String,
    navigateToSongs: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbnail = getCoverArt(LocalContext.current, coverArt)

    Surface(
        modifier = modifier.width(150.dp),
        shape = MaterialTheme.shapes.small,
        onClick = {
            navigateToSongs(id)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = if (thumbnail == null) painterResource(id = R.drawable.musica) else rememberAsyncImagePainter(model = coverArt),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(shape = MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

fun getCoverArt(context: Context, coverArt: String): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.loadThumbnail(
                Uri.parse(coverArt),
                Size(300, 300),
                null
            )
        } else {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(coverArt))
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (ex: Exception) {
        null
    }
}