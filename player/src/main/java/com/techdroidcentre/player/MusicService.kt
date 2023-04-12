package com.techdroidcentre.player

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.techdroidcentre.data.*
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

    private lateinit var playbackNotification: PlaybackNotification

    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()

    var isForegroundService = false

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

        val intent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession = MediaSessionCompat(this, TAG).apply {
            isActive = true
            setSessionActivity(intent)
        }
        sessionToken = mediaSession.sessionToken

        playbackNotification = PlaybackNotification(this, mediaSession, notificationListener)

        serviceScope.launch {
            musicSource.fetchSongs()
        }
//        exoplayer.addListener(MusicPlayerEventListener())

        val musicPlaybackPreparer = MusicPlaybackPreparer(musicSource, deleteSong) { mediaMetaData, playWhenReady, parentId, songIds ->
            val mediaMetaDataList = buildPlayList(mediaMetaData, parentId, songIds)
            currentPlaylistItems = mediaMetaDataList
            val currentItemIndex = if (mediaMetaData == null) 0 else mediaMetaDataList.indexOf(mediaMetaData)
            val playbackPosition = 0L
            exoplayer.playWhenReady = playWhenReady
            exoplayer.setMediaItems(mediaMetaDataList.map { it.toMediaItem() })
            exoplayer.seekTo(currentItemIndex, playbackPosition)
            exoplayer.prepare()
//            if (playWhenReady)
//                playbackNotification.showNotification(exoplayer)
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator(mediaSession))
        mediaSessionConnector.setPlayer(exoplayer)
        playbackNotification.showNotification(exoplayer)
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }
        exoplayer.release()
        serviceJob.cancel()
        stopForeground(true)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoplayer.stop()
        exoplayer.clearMediaItems()
        playbackNotification.hideNotification()
    }

    private fun buildPlayList(
        itemToPlay: MediaMetadataCompat,
        parentId: String?,
        songIds: ArrayList<String>?
    ): List<MediaMetadataCompat> {
        if (songIds != null && songIds.isNotEmpty()) {
            return musicSource.songs.filter {
                songIds.contains(it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) ?: false
            }
        }
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

    inner class MusicQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return if (windowIndex < currentPlaylistItems.size) {
                currentPlaylistItems[windowIndex].description
            } else {
                MediaDescriptionCompat.Builder().build()
            }
        }
    }

    private val deleteSong = { parentId: String, mediaUri: String ->
        val albumId = browseRoot.getAlbumIdForSong(parentId, mediaUri)
        val artistId = browseRoot.getArtistIdForSong(parentId, mediaUri)
        browseRoot.deleteSong(parentId, mediaUri)
        notifyChildrenChanged(ALBUMS_ROOT)
        notifyChildrenChanged(ARTISTS_ROOT)
        notifyChildrenChanged(SONGS_ROOT)
        notifyChildrenChanged(albumId)
        notifyChildrenChanged(artistId)
    }

    private val notificationListener = object: PlayerNotificationManager.NotificationListener{
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }
    }
}