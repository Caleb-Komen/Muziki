package com.techdroidcentre.data

import com.google.common.truth.Truth
import com.techdroidcentre.data.queries.FakeMediaQueryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MusicSourceTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var mediaQuery: FakeMediaQueryImpl

    private lateinit var musicSource: MusicSource

    var waiting = true

    @Before
    fun setup() {
        mediaQuery = FakeMediaQueryImpl()
        musicSource = MusicSource(mediaQuery, dispatcher)
    }

    @Test
    fun fetchSongs_success() = runTest{
        musicSource.fetchSongs()
        Truth.assertThat(musicSource.songs.size).isAtLeast(1)
    }

    @Test
    fun fetchSongs_error() = runTest{
        mediaQuery.setNull()
        musicSource.fetchSongs()
        Truth.assertThat(musicSource.songs.size).isEqualTo(0)
    }

    @Test
    fun fetchAlbums_success() = runTest{
        musicSource.fetchAlbums()
        Truth.assertThat(musicSource.albums.size).isAtLeast(1)
    }

    @Test
    fun fetchAlbums_error() = runTest{
        mediaQuery.setNull()
        musicSource.fetchAlbums()
        Truth.assertThat(musicSource.albums.size).isEqualTo(0)
    }

    @Test
    fun fetchArtists_success() = runTest{
        musicSource.fetchArtists()
        Truth.assertThat(musicSource.artists.size).isAtLeast(1)
    }

    @Test
    fun fetchArtists_error() = runTest{
        mediaQuery.setNull()
        musicSource.fetchArtists()
        Truth.assertThat(musicSource.artists.size).isEqualTo(0)
    }

    @Test
    fun whenReady_noWaiting() = runTest{
        waiting = true
        musicSource.fetchSongs()
        musicSource.whenReady { success ->
            Truth.assertThat(success).isTrue()
            waiting = false
        }
        Truth.assertThat(waiting).isFalse()
    }

    @Test
    fun whenReady_waits() = runTest{
        waiting = true
        musicSource.whenReady { success ->
            Truth.assertThat(success).isTrue()
            waiting = false
        }
        Truth.assertThat(waiting).isTrue()
        musicSource.fetchSongs()
        Truth.assertThat(waiting).isFalse()
    }

    @Test
    fun whenReady_error() = runTest{
        waiting = true
        musicSource.whenReady { success ->
            Truth.assertThat(success).isFalse()
            waiting = false
        }
        Truth.assertThat(waiting).isTrue()
        mediaQuery.setNull()
        musicSource.fetchSongs()
        Truth.assertThat(waiting).isFalse()
    }
}