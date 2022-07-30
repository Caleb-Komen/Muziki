package com.techdroidcentre.data.di

import android.content.Context
import com.techdroidcentre.data.queries.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(ServiceComponent::class)
@Module
object ServiceModule {
    @ServiceScoped
    @Provides
    fun provideMediaQuery(
        songsQuery: SongsQuery,
        albumQuery: AlbumQuery,
        artistQuery: ArtistQuery,
        @ApplicationContext context: Context
    ): MediaQuery {
        return MediaQueryImpl(songsQuery, albumQuery, artistQuery, context)
    }

    @ServiceScoped
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}