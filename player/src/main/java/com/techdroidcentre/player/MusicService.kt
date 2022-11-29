package com.techdroidcentre.player

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.techdroidcentre.data.BROWSABLE_ROOT
import com.techdroidcentre.data.BrowseRoot
import com.techdroidcentre.data.MusicSource
import com.techdroidcentre.data.util.METADATA_KEY_ALBUM_ID
import com.techdroidcentre.data.util.METADATA_KEY_ARTIST_ID
import com.techdroidcentre.data.util.METADATA_KEY_FLAG
import com.techdroidcentre.player.mapper.toMediaItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MusicService"

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {
    private lateinit var mediaSession: MediaSessionCompat

    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    @Inject
    lateinit var exoplayer: ExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    private val browseRoot: BrowseRoot by lazy {
        BrowseRoot(applicationContext, musicSource)
    }

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(this, TAG).apply {
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        serviceScope.launch {
            musicSource.fetchSongs()
        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(musicSource) { mediaMetaData, playWhenReady, parentId ->
            val mediaMetaDataList = buildPlayList(mediaMetaData, parentId)
            currentPlaylistItems = mediaMetaDataList
            val currentItemIndex = if (musicSource.songs.indexOf(mediaMetaData) == -1) 0 else musicSource.songs.indexOf(
                mediaMetaData
            )
            val playbackPosition = 0L
            exoplayer.playWhenReady = playWhenReady
            exoplayer.setMediaItems(mediaMetaDataList.map { it.toMediaItem() })
            exoplayer.seekTo(currentItemIndex, playbackPosition)
            exoplayer.prepare()
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.apply {
            setPlayer(exoplayer)
            setPlaybackPreparer(musicPlaybackPreparer)
        }
    }

    override fun onDestroy() {
        serviceJob.cancel()
    }

    private fun buildPlayList(itemToPlay: MediaMetadataCompat, parentId: String?): List<MediaMetadataCompat> {
        val albumId = itemToPlay.getLong(METADATA_KEY_ALBUM_ID).toString()
        val artistId = itemToPlay.getLong(METADATA_KEY_ARTIST_ID).toString()
        return when (parentId) {
            albumId -> musicSource.songs.filter {
                it.getLong(METADATA_KEY_ALBUM_ID).toString() == parentId

            }
            artistId -> musicSource.songs.filter {
                it.getLong(METADATA_KEY_ARTIST_ID).toString() == parentId
            }
            else -> musicSource.songs
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(BROWSABLE_ROOT, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val ready = musicSource.whenReady { success ->
            if (success) {
                val children = browseRoot.mediaIdToChildren[parentId]?.map {
                    MediaBrowserCompat.MediaItem(it.description, it.getLong(METADATA_KEY_FLAG).toInt())
                }?.toMutableList()
                result.sendResult(children)
            } else {
                result.sendResult(null)
            }
        }

        if(!ready) {
            result.detach()
        }

    }

}