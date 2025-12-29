package com.helpofai.mymmusic.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "playback_history")
data class RecentTrack(
    @PrimaryKey val trackId: Long,
    val lastPlayedTimestamp: Long
)

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(track: RecentTrack)

    @Query("SELECT * FROM playback_history ORDER BY lastPlayedTimestamp DESC LIMIT 20")
    fun getRecentTrackIds(): Flow<List<RecentTrack>>
}

@Database(entities = [RecentTrack::class], version = 1)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
