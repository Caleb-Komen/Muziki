package com.techdroidcentre.musicplayer.di

import android.content.ComponentName
import android.content.Context
import com.techdroidcentre.player.MusicService
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
    fun provideComponentName(@ApplicationContext context: Context): ComponentName {
        return ComponentName(
            context,
            MusicService::class.java
        )
    }
}