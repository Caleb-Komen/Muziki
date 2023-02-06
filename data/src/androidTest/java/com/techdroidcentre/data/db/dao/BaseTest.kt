package com.techdroidcentre.data.db.dao

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.techdroidcentre.data.db.PlayListDatabase
import org.junit.After
import org.junit.Before

open class BaseTest {
    lateinit var database: PlayListDatabase
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        database = Room.inMemoryDatabaseBuilder(context, PlayListDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()
}