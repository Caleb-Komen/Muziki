package com.techdroidcentre.data.di

import android.content.Context
import androidx.room.Room
import com.techdroidcentre.data.db.PlayListDatabase
import com.techdroidcentre.data.db.dao.PlayListDao
import com.techdroidcentre.data.db.dao.PlayListSongDao
import com.techdroidcentre.data.repository.PlayListRepository
import com.techdroidcentre.data.repository.PlayListRepositoryImpl
import com.techdroidcentre.data.repository.PlayListSongRepository
import com.techdroidcentre.data.repository.PlayListSongRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun providePlaylistDatabase(
        @ApplicationContext context: Context
    ): PlayListDatabase {
        return Room.databaseBuilder(
            context,
            PlayListDatabase::class.java,
            PlayListDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun providePlayListDao(database: PlayListDatabase): PlayListDao = database.playListDao

    @Singleton
    @Provides
    fun providePlaySongListDao(database: PlayListDatabase): PlayListSongDao = database.playListSongDao

    @Singleton
    @Provides
    fun providePlaylistRepository(playlistDao: PlayListDao): PlayListRepository {
        return PlayListRepositoryImpl(playlistDao)
    }

    @Singleton
    @Provides
    fun providePlaylistSongRepository(playlistSongDao: PlayListSongDao): PlayListSongRepository {
        return PlayListSongRepositoryImpl(playlistSongDao)
    }
}