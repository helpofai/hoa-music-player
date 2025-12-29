package com.helpofai.mymmusic.di

import android.content.Context
import androidx.room.Room
import com.helpofai.mymmusic.data.local.HistoryDao
import com.helpofai.mymmusic.data.local.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_db"
        ).build()
    }

    @Provides
    fun provideHistoryDao(database: MusicDatabase): HistoryDao {
        return database.historyDao()
    }
}
