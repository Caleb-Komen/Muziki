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
import org.robolectric.RuntimeEnvironment

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MusicSourceTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var mediaQuery: FakeMediaQueryImpl

    private lateinit var musicSource: MusicSource

    var waiting = true

    @Before
    fun setup() {
        val app = RuntimeEnvironment.getApplication()
        mediaQuery = FakeMediaQueryImpl()
        musicSource = MusicSource(mediaQuery, dispatcher, app)
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