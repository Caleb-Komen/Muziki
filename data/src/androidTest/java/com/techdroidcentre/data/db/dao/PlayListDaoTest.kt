package com.techdroidcentre.data.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.techdroidcentre.data.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PlayListDaoTest: BaseTest() {
    private lateinit var playListDao: PlayListDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        playListDao = database.playListDao
    }

    @Test
    fun createPlaylist() = runTest {
        val playlist = playlistWithSongs1.playListEntity
        playListDao.createPlaylist(playlist)

        val result = playListDao.getPlayLists().getOrAwaitValue()
        Truth.assertThat(result).contains(playlist)
    }

    @Test
    fun createPlaylist_ignoreOnConflict() = runTest {
        val playlist1 = playlistWithSongs1.playListEntity
        val playlist2 = playlistWithSongs1.playListEntity.copy(name = "Country")
        playListDao.createPlaylist(playlist1)
        playListDao.createPlaylist(playlist2)

        val result = playListDao.getPlayLists().getOrAwaitValue()
        Truth.assertThat(result).contains(playlist1)
        Truth.assertThat(result).doesNotContain(playlist2)
    }

    @Test
    fun updatePlayList() = runTest {
        val playlist = playlistWithSongs1.playListEntity
        playListDao.createPlaylist(playlist)

        val updatedPlayList = playlist.copy(name = "Country")
        playListDao.updatePlayList(updatedPlayList)

        val result = playListDao.getPlayLists().getOrAwaitValue()
        Truth.assertThat(result[0].name).isEqualTo("Country")
    }

    @Test
    fun deletePlayList() = runTest {
        val playlist1 = playlistWithSongs1.playListEntity
        val playlist2 = playlistWithSongs2.playListEntity
        playListDao.createPlaylist(playlist1)
        playListDao.createPlaylist(playlist2)

        var result = playListDao.getPlayLists().getOrAwaitValue()
        Truth.assertThat(result.size).isEqualTo(2)
        Truth.assertThat(result).contains(playlist1)
        Truth.assertThat(result).contains(playlist2)

        playListDao.deletePlayList(playlist1.id)

        result = playListDao.getPlayLists().getOrAwaitValue()
        Truth.assertThat(result.size).isEqualTo(1)
        Truth.assertThat(result).doesNotContain(playlist1)
        Truth.assertThat(result).contains(playlist2)
    }
}