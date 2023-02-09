package com.techdroidcentre.data.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.techdroidcentre.data.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PlayListSongDaoTest: BaseTest() {
    private lateinit var playListDao: PlayListDao
    private lateinit var playListSongDao: PlayListSongDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() = runBlocking{
        playListDao = database.playListDao
        playListSongDao = database.playListSongDao

        playListDao.createPlaylist(playlistWithSongs1.playListEntity)
    }

    @Test
    fun addSong() = runTest {
        val song = playlistWithSongs1.playListSongsEntity[0]
        playListSongDao.addSong(song)

        val result = playListSongDao.getPlayListSongs(song.playListId).getOrAwaitValue()
        Truth.assertThat(result).contains(song)
    }

    @Test
    fun addSong_ignoreOnConflict() = runTest {
        val song1 = playlistWithSongs1.playListSongsEntity[0]
        val song2 = song1.copy(album = "Unknown")
        playListSongDao.addSong(song1)
        playListSongDao.addSong(song2)

        val result = playListSongDao.getPlayListSongs(song1.playListId).getOrAwaitValue()
        Truth.assertThat(result).contains(song1)
        Truth.assertThat(result).doesNotContain(song2)
    }

    @Test
    fun deleteSong() = runTest {
        val song1 = playlistWithSongs1.playListSongsEntity[0]
        val song2 = playlistWithSongs1.playListSongsEntity[1]
        playListSongDao.addSong(song1)
        playListSongDao.addSong(song2)

        var result = playListSongDao.getPlayListSongs(song1.playListId).getOrAwaitValue()
        Truth.assertThat(result.size).isEqualTo(2)
        Truth.assertThat(result).contains(song1)
        Truth.assertThat(result).contains(song2)

        playListSongDao.deleteSong(song1.mediaUri)
        result = playListSongDao.getPlayListSongs(song1.playListId).getOrAwaitValue()
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result).doesNotContain(song1)
        Truth.assertThat(result).contains(song2)
    }
}